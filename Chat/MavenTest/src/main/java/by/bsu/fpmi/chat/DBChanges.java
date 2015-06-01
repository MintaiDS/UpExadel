package by.bsu.fpmi.chat;


import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NotePad on 24.05.2015.
 */
public class DBChanges {
    public static boolean checkUser(String user, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean isGood = false;
        try {
            connection = ConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM " + Functions.DBUSERS + " WHERE user = ?");
            preparedStatement.setString(1, user);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                String pass = resultSet.getString("password");
                boolean inUse = resultSet.getBoolean("inUse");
                String salt = Functions.createSalt(user);
                password = Functions.hashAndSalt(salt,password);
                if (password.equals(pass) && !inUse)
                {
                    preparedStatement = connection.prepareStatement("UPDATE " + Functions.DBUSERS +" SET inUse = TRUE WHERE user = ?");
                    preparedStatement.setString(1, user);
                    preparedStatement.executeUpdate();
                    isGood = true;
                }
            }
        } catch (SQLException e) {
            MyServlet.logger.error(e);
        }  catch (NoSuchAlgorithmException e) {
            MyServlet.logger.error(e);
        }  finally {
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
        return isGood;
    }
    public static boolean addUser(String user, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean isGood = true;
        try {
            connection = ConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM " + Functions.DBUSERS + " WHERE user = ?");
            preparedStatement.setString(1, user);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                    isGood = false;
            }
            else {
                String salt = Functions.createSalt(user);
                password = Functions.hashAndSalt(salt,password);
                preparedStatement = connection.prepareStatement("INSERT INTO " + Functions.DBUSERS + "(user, password, inUse) VALUES(?,?,FALSE)");
                preparedStatement.setString(1, user);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            MyServlet.logger.error(e);
        } catch (NoSuchAlgorithmException e) {
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
        return isGood;
    }
    public static void add(String db, Message m) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO " + db + " (message_id, text, username, user, data, status) VALUES (?, ?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, Integer.parseInt(m.getMessageId()));
            preparedStatement.setString(2, m.getMessageText());
            preparedStatement.setString(3, m.getUsername());
            preparedStatement.setString(4, m.getUser());
            preparedStatement.setString(5, m.getDate());
            preparedStatement.setString(6, m.getStatus());
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
    public static void freeUser(String user) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE " + Functions.DBUSERS + " SET inUse = FALSE WHERE user = ?");
            preparedStatement.setString(1, user);
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
            connection = ConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE messages SET text = ? WHERE message_id = ?");
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
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM "+db);
            while (resultSet.next()) {
                String messageId = new Integer(resultSet.getInt("message_id")).toString();
                String messageText = resultSet.getString("text");
                String username = resultSet.getString("username");
                String user = resultSet.getString("user");
                String data = resultSet.getString("data");
                String status = resultSet.getString("status");
                messages.add(new Message(messageId, username, user, messageText, data, status));
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
            connection = ConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM messages WHERE message_id = ?");
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
            connection = ConnectionPool.getConnection();
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
    public static void freeAll() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE " + Functions.DBUSERS + " SET inUse = FALSE ");
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
