package by.bsu.fpmi.chat;

/**
 * Created by NotePad on 24.05.2015.
 */
import java.sql.*;

public class ConnectionManager {
    private static final String URL = "jdbc:mysql://localhost:3306/chat";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    public static void checkDB() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, Functions.DB, null);

            if (!tables.next()) {
                preparedStatement = connection.prepareStatement("CREATE TABLE " + Functions.DB + " (message_id Integer, text varchar(255), username varchar(255), data varchar(255), status varchar(255), PRIMARY KEY (message_id))");
                preparedStatement.executeUpdate();
            } else {
                Statement statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT message_id from messages ORDER BY message_id DESC;");
                resultSet.next();
                MessageStorage.setCurId(resultSet.getInt("message_id"));
                MessageStorage.incId();
                //System.out.println(MessageStorage.getCurId());
            }
            tables = dbm.getTables(null, null, Functions.DBCHANGES, null);
            if (!tables.next()) {
                preparedStatement = connection.prepareStatement("CREATE TABLE " + Functions.DBCHANGES + " (message_id Integer, text varchar(255), username varchar(255), data varchar(255), status varchar(255), PRIMARY KEY (message_id))");
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            MyServlet.logger.error(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    MyServlet.logger.error(e);
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    MyServlet.logger.error(e);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    MyServlet.logger.error(e);
                }
            }
        }
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            MyServlet.logger.error(e);
        }
        return connection;
    }

}