package be.vsol.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RS {

    private final ResultSet resultSet;

    public RS(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public boolean next() {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void close() {
        try {
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Object getObject(int index) {
        try {
            return resultSet.getObject(index + 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getString(int index) {
        try {
            return resultSet.getString(index + 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getInt(int index) {
        try {
            return resultSet.getInt(index + 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean getBoolean(int index) {
        try {
            return resultSet.getBoolean(index + 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long getLong(int index) {
        try {
            return resultSet.getLong(index + 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Double getDouble(int index) {
        try {
            return resultSet.getDouble(index + 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getObject(String column) {
        try {
            return resultSet.getObject(column);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getString(String column) {
        try {
            return resultSet.getString(column);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getInt(String column) {
        try {
            return resultSet.getInt(column);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean getBoolean(String column) {
        try {
            return resultSet.getBoolean(column);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public long getLong(String column) {
        try {
            return resultSet.getLong(column);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public short getShort(String column) {
        try {
            return resultSet.getShort(column);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (short) 0;
    }

    public float getFloat(String column) {
        try {
            return resultSet.getFloat(column);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0f;
    }

    public double getDouble(String column) {
        try {
            return resultSet.getDouble(column);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // GETTERS

    public ResultSet get() { return resultSet; }
}
