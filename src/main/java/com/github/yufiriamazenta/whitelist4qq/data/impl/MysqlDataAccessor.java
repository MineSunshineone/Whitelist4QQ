package com.github.yufiriamazenta.whitelist4qq.data.impl;

import com.github.yufiriamazenta.whitelist4qq.config.Configs;
import com.github.yufiriamazenta.whitelist4qq.data.DataAccessor;
import com.github.yufiriamazenta.whitelist4qq.data.exception.DataAccessException;
import crypticlib.CrypticLibBukkit;
import crypticlib.DataSource;
import crypticlib.libs.hikari.HikariConfig;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public enum MysqlDataAccessor implements DataAccessor {

    INSTANCE;

    private final DataSource dataSource = new DataSource();

    @Override
    public void reload(Plugin plugin) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(Configs.mysqlDriver.value());
        config.setConnectionTimeout(Configs.mysqlHikariConnTimeout.value());
        config.setMinimumIdle(Configs.mysqlHikariMinIdle.value());
        config.setMaximumPoolSize(Configs.mysqlHikariMaxPoolSize.value());
        config.setMaxLifetime(Configs.mysqlHikariMaxLifetime.value());
        config.setAutoCommit(Configs.mysqlHikariAutoCommit.value());
        config.setJdbcUrl(Configs.mysqlUrl.value());
        config.setUsername(Configs.mysqlUsername.value());
        config.setPassword(Configs.mysqlPassword.value());
        dataSource.setHikariConfig(config, true);
        createTable(plugin);
    }

    private void createTable(Plugin plugin) {
        CrypticLibBukkit.scheduler().runTaskAsync(plugin, () -> {
            try(Connection conn = checkConn(dataSource.getConn())) {
                PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS ? (uuid varchar(36) NOT NULL, qqid long NOT NULL);");
                ps.setString(1, Configs.mysqlTable.value());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e);
            }
        });
    }

    @Override
    public boolean addBind(UUID uuid, long qq) {
        try(Connection conn = checkConn(dataSource.getConn())) {
            PreparedStatement ps = conn.prepareStatement("replace into ? (`uuid`, `qqid`) values (?, ?);");
            ps.setString(1, Configs.mysqlTable.value());
            ps.setString(2, uuid.toString());
            ps.setLong(3, qq);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public boolean removeBind(UUID uuid) {
        try(Connection conn = checkConn(dataSource.getConn())) {
            PreparedStatement ps = conn.prepareStatement("delete from ? where `uuid` = ?;");
            ps.setString(1, Configs.mysqlTable.value());
            ps.setString(2, uuid.toString());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public boolean removeBind(long qq) {
        try(Connection conn = checkConn(dataSource.getConn())) {
            PreparedStatement ps = conn.prepareStatement("delete from ? where `qqid` = ?;");
            ps.setString(1, Configs.mysqlTable.value());
            ps.setLong(2, qq);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public UUID getBind(long qq) {
        try (Connection conn = checkConn(dataSource.getConn())) {
            PreparedStatement ps = conn.prepareStatement(
                "select `uuid` from ? where `qqid` = ?;"
            );
            ps.setString(1, Configs.mysqlTable.value());
            ps.setLong(2, qq);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return UUID.fromString(result.getString("uuid"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getBind(UUID uuid) {
        try (Connection conn = checkConn(dataSource.getConn())) {
            PreparedStatement ps = conn.prepareStatement(
                "select `qqid` from ? where `uuid` = ?;"
            );
            ps.setString(1, Configs.mysqlTable.value());
            ps.setString(2, uuid.toString());
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return result.getLong("qqid");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private @NotNull Connection checkConn(Connection conn) {
        if (conn == null) {
            throw new DataAccessException("Could not get connection");
        }
        return conn;
    }

}
