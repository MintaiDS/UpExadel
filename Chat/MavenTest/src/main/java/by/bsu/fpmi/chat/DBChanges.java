package by.bsu.fpmi.chat;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NotePad on 24.05.2015.
 */
public class DBChanges {

    public static void add(String db, Message m) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO " + db + " (message_id, text, username, data, status) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, Integer.parseInt(m.getMessageId()));
            preparedStatement.setString(2, m.getMessageText());
            preparedStatement.setString(3, m.getUsername());
            preparedStatement.setString(4, m.getDate());
            preparedStatement.setString(5, m.getStatus());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            MyServlet.logger.error(e);
        } finally {
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

    public static void update(Message m) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement("Update messages SET text = ? WHERE message_id = ?");
            preparedStatement.setString(1, m.getMessageText());
            preparedStatement.setInt(2, Integer.parseInt(m.getMessageId()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            MyServlet.logger.error(e);
        } finally {
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

    public static List<Message> selectAll(String db) {
        List<Message> messages = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionManager.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM "+db);
            while (resultSet.next()) {
                String messageId = new Integer(resultSet.getInt("message_id")).toString();
                String messageText = resultSet.getString("text");
                String username = resultSet.getString("username");
                String data = resultSet.getString("data");
                String status = resultSet.getString("status");
                messages.add(new Message(messageId, username, messageText, data, status));
            }
        } catch (SQLException e) {
            MyServlet.logger.error(e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    MyServlet.logger.error(e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
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
        return messages;
    }

    public static void delete(int id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement("delete from messages WHERE message_id = ?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            MyServlet.logger.error(e);
        } finally {
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

    public static void deleteAll(String db) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionManager.getConnection();
            preparedStatement = connection.prepareStatement("delete from " + db);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            MyServlet.logger.error(e);
        } finally {
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
}
