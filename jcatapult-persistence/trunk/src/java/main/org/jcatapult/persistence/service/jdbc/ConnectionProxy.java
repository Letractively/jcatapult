/*
 * Copyright (c) 2001-2010, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.jcatapult.persistence.service.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

import com.google.inject.Inject;

/**
 * <p> This class is a JDBC connection proxy. It only calls to the ConnectionProvider when it is absolutely necessary.
 * This delays the creating of a connection until it is actually going to be used. </p>
 *
 * @author Brian Pontarelli
 */
public class ConnectionProxy implements Connection {
  private final ConnectionProvider provider;
  private Connection proxy;

  @Inject
  public ConnectionProxy(ConnectionProvider provider) {
    this.provider = provider;
  }

  @Override
  public Statement createStatement() throws SQLException {
    proxy = provider.get();
    return proxy.createStatement();
  }

  @Override
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    proxy = provider.get();
    return proxy.prepareStatement(sql);
  }

  @Override
  public CallableStatement prepareCall(String sql) throws SQLException {
    proxy = provider.get();
    return proxy.prepareCall(sql);
  }

  @Override
  public String nativeSQL(String sql) throws SQLException {
    proxy = provider.get();
    return proxy.nativeSQL(sql);
  }

  @Override
  public void setAutoCommit(boolean autoCommit) throws SQLException {
    proxy = provider.get();
    proxy.setAutoCommit(autoCommit);
  }

  @Override
  public boolean getAutoCommit() throws SQLException {
    proxy = provider.get();
    return proxy.getAutoCommit();
  }

  @Override
  public void commit() throws SQLException {
    proxy = provider.get();
    proxy.commit();
  }

  @Override
  public void rollback() throws SQLException {
    proxy = provider.get();
    proxy.rollback();
  }

  @Override
  public void close() throws SQLException {
    proxy = provider.get();
    proxy.close();
  }

  @Override
  public boolean isClosed() throws SQLException {
    proxy = provider.get();
    return proxy.isClosed();
  }

  @Override
  public DatabaseMetaData getMetaData() throws SQLException {
    proxy = provider.get();
    return proxy.getMetaData();
  }

  @Override
  public void setReadOnly(boolean readOnly) throws SQLException {
    proxy = provider.get();
    proxy.setReadOnly(readOnly);
  }

  @Override
  public boolean isReadOnly() throws SQLException {
    proxy = provider.get();
    return proxy.isReadOnly();
  }

  @Override
  public void setCatalog(String catalog) throws SQLException {
    proxy = provider.get();
    proxy.setCatalog(catalog);
  }

  @Override
  public String getCatalog() throws SQLException {
    proxy = provider.get();
    return proxy.getCatalog();
  }

  @Override
  public void setTransactionIsolation(int level) throws SQLException {
    proxy = provider.get();
    proxy.setTransactionIsolation(level);
  }

  @Override
  public int getTransactionIsolation() throws SQLException {
    proxy = provider.get();
    return proxy.getTransactionIsolation();
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    proxy = provider.get();
    return proxy.getWarnings();
  }

  @Override
  public void clearWarnings() throws SQLException {
    proxy = provider.get();
    proxy.clearWarnings();
  }

  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
    proxy = provider.get();
    return proxy.createStatement(resultSetType, resultSetConcurrency);
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    proxy = provider.get();
    return proxy.prepareStatement(sql, resultSetType, resultSetConcurrency);
  }

  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    proxy = provider.get();
    return proxy.prepareCall(sql, resultSetType, resultSetConcurrency);
  }

  @Override
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    proxy = provider.get();
    return proxy.getTypeMap();
  }

  @Override
  public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
    proxy = provider.get();
    proxy.setTypeMap(map);
  }

  @Override
  public void setHoldability(int holdability) throws SQLException {
    proxy = provider.get();
    proxy.setHoldability(holdability);
  }

  @Override
  public int getHoldability() throws SQLException {
    proxy = provider.get();
    return proxy.getHoldability();
  }

  @Override
  public Savepoint setSavepoint() throws SQLException {
    proxy = provider.get();
    return proxy.setSavepoint();
  }

  @Override
  public Savepoint setSavepoint(String name) throws SQLException {
    proxy = provider.get();
    return proxy.setSavepoint(name);
  }

  @Override
  public void rollback(Savepoint savepoint) throws SQLException {
    proxy = provider.get();
    proxy.rollback(savepoint);
  }

  @Override
  public void releaseSavepoint(Savepoint savepoint) throws SQLException {
    proxy = provider.get();
    proxy.releaseSavepoint(savepoint);
  }

  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    proxy = provider.get();
    return proxy.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    proxy = provider.get();
    return proxy.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
  }

  @Override
  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    proxy = provider.get();
    return proxy.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
    proxy = provider.get();
    return proxy.prepareStatement(sql, autoGeneratedKeys);
  }

  @Override
  public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
    proxy = provider.get();
    return proxy.prepareStatement(sql, columnIndexes);
  }

  @Override
  public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
    proxy = provider.get();
    return proxy.prepareStatement(sql, columnNames);
  }

  @Override
  public Clob createClob() throws SQLException {
    proxy = provider.get();
    return proxy.createClob();
  }

  @Override
  public Blob createBlob() throws SQLException {
    proxy = provider.get();
    return proxy.createBlob();
  }

  @Override
  public NClob createNClob() throws SQLException {
    proxy = provider.get();
    return proxy.createNClob();
  }

  @Override
  public SQLXML createSQLXML() throws SQLException {
    proxy = provider.get();
    return proxy.createSQLXML();
  }

  @Override
  public boolean isValid(int timeout) throws SQLException {
    proxy = provider.get();
    return proxy.isValid(timeout);
  }

  @Override
  public void setClientInfo(String name, String value) throws SQLClientInfoException {
    proxy = provider.get();
    proxy.setClientInfo(name, value);
  }

  @Override
  public void setClientInfo(Properties properties) throws SQLClientInfoException {
    proxy = provider.get();
    proxy.setClientInfo(properties);
  }

  @Override
  public String getClientInfo(String name) throws SQLException {
    proxy = provider.get();
    return proxy.getClientInfo(name);
  }

  @Override
  public Properties getClientInfo() throws SQLException {
    proxy = provider.get();
    return proxy.getClientInfo();
  }

  @Override
  public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
    proxy = provider.get();
    return proxy.createArrayOf(typeName, elements);
  }

  @Override
  public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
    proxy = provider.get();
    return proxy.createStruct(typeName, attributes);
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    proxy = provider.get();
    return proxy.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    proxy = provider.get();
    return proxy.isWrapperFor(iface);
  }
}
