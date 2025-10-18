package main;

import dao.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class TestConnection {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("TEST DE CONEXIÓN A MYSQL");
        System.out.println("============================");
        System.out.print("Ingresa la contraseña de MySQL: ");
        String password = scanner.nextLine();

        try {
            DatabaseConnection.setPassword(password);

            System.out.println("Intentando conectar a la base de datos...");
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("¡Conexión exitosa!");

            Statement stmt = conn.createStatement();

            System.out.println("\nVerificando tablas...");
            ResultSet rs = stmt.executeQuery("SHOW TABLES");

            boolean hasTables = false;
            System.out.println("Tablas encontradas:");
            while (rs.next()) {
                hasTables = true;
                System.out.println("  - " + rs.getString(1));
            }

            if (!hasTables) {
                System.out.println("No se encontraron tablas. Necesitas ejecutar el script SQL.");
                System.out.println("Ejecuta el script que te proporcioné para crear las tablas.");
            } else {
                System.out.println("¡Base de datos configurada correctamente!");
            }

            conn.close();
            System.out.println("\nTest completado exitosamente!");

        } catch (Exception e) {
            System.out.println("Error en la conexión: " + e.getMessage());
            System.out.println("\nPosibles soluciones:");
            System.out.println("1. Verifica que MySQL esté ejecutándose");
            System.out.println("2. Verifica que la base de datos 'recetas_medicas' exista");
            System.out.println("3. Verifica el usuario y contraseña");
            System.out.println("4. Verifica que el puerto 3306 esté abierto");

            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}