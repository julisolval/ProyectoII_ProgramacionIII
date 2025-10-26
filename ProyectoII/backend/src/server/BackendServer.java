package server;

import config.Config;
import org.jfree.data.json.impl.JSONObject;
import service.Service;
import dao.DatabaseConnection;
import service.ServiceImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BackendServer {
    private static final int PORT = Config.SERVER_PORT;
    private final List<ClientHandler> clients;
    private final Service service;

    public BackendServer(String dbPassword) {
        this.clients = Collections.synchronizedList(new ArrayList<>());
        DatabaseConnection.setPassword(dbPassword);
        this.service = new ServiceImpl();

        this.service.limpiarUsuariosActivos();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingresa la contraseña de MySQL: ");
        String password = scanner.nextLine();

        try {
            DatabaseConnection.setPassword(password);
            var testConn = DatabaseConnection.getConnection();
            testConn.close();
            System.out.println("Conexión a BD exitosa");
        } catch (Exception e) {
            System.err.println("Error conectando a la base de datos: " + e.getMessage());
            System.out.println("¿Quieres intentar con otra contraseña? (s/n)");
            String respuesta = scanner.nextLine();
            if (respuesta.equalsIgnoreCase("s")) {
                main(args);
                return;
            } else {
                System.exit(1);
            }
        }

        new BackendServer(password).start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    public Service getService() {
        return service;
    }

    public void broadcastToAll(org.json.JSONObject message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client.isConnected()) {
                    client.sendMessage(message);
                }
            }
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Cliente desconectado. Clientes activos: " + clients.size());
    }

    public void notifyUserLogin(String username, String nombre) {
        org.json.JSONObject notification = new org.json.JSONObject();
        notification.put("tipo", "notificacion");
        notification.put("subtipo", "usuario_conectado");

        org.json.JSONObject datos = new org.json.JSONObject();
        datos.put("username", username);
        datos.put("nombre", nombre);
        notification.put("datos", datos);

        broadcastToAll(notification);
        System.out.println("Notificando conexión de: " + username);
    }

    public void notifyUserLogout(String username) {
        org.json.JSONObject notification = new org.json.JSONObject();
        notification.put("tipo", "notificacion");
        notification.put("subtipo", "usuario_desconectado");

        org.json.JSONObject datos = new org.json.JSONObject();
        datos.put("username", username);
        notification.put("datos", datos);

        broadcastToAll(notification);
        System.out.println("Notificando desconexión de: " + username);
    }

    public void notifyNewMessage(String remitente, String destinatario, String texto) {
        ClientHandler destHandler = findClientByUsername(destinatario);

        // ✅ Buscar nombre del remitente
        String nombreRemitente = remitente;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT nombre FROM Usuario WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, remitente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nombreRemitente = rs.getString("nombre");
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo nombre remitente: " + e.getMessage());
        }

        if (destHandler != null && destHandler.isConnected()) {
            org.json.JSONObject notification = new org.json.JSONObject();
            notification.put("tipo", "notificacion");
            notification.put("subtipo", "mensaje_recibido");

            org.json.JSONObject datosNotif = new org.json.JSONObject();
            datosNotif.put("remitente", remitente);
            datosNotif.put("nombre_remitente", nombreRemitente);
            datosNotif.put("destinatario", destinatario);
            datosNotif.put("texto", texto);
            datosNotif.put("fecha", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            notification.put("datos", datosNotif);

            destHandler.sendMessage(notification);
            System.out.println("✅ Notificación enviada a: " + destinatario);
        } else {
            System.out.println("ℹ️ Destinatario " + destinatario + " no conectado, mensaje guardado");
        }
    }

    private ClientHandler findClientByUsername(String username) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (username.equals(client.getUsername())) {
                    return client;
                }
            }
        }
        return null;
    }
}

