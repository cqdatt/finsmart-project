package com.finsmart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Base interface for all DAO classes
 * Defines common CRUD operations contract
 * @param <T> The entity type
 * @param <ID> The primary key type
 */
public interface IDao<T, ID> {
    
    /**
     * Find entity by its primary key
     * @param id the primary key value
     * @return the entity if found, null otherwise
     * @throws SQLException if database access error occurs
     * @throws ClassNotFoundException if JDBC driver not found
     */
    T findById(ID id) throws SQLException, ClassNotFoundException;
    
    /**
     * Find all entities
     * @return list of all entities, empty list if none found
     * @throws SQLException if database access error occurs
     * @throws ClassNotFoundException if JDBC driver not found
     */
    List<T> findAll() throws SQLException, ClassNotFoundException;
    
    /**
     * Insert a new entity
     * @param entity the entity to insert
     * @return true if insert successful, false otherwise
     * @throws SQLException if database access error occurs
     * @throws ClassNotFoundException if JDBC driver not found
     */
    boolean create(T entity) throws SQLException, ClassNotFoundException;
    
    /**
     * Update an existing entity
     * @param entity the entity with updated values
     * @return true if update successful, false otherwise
     * @throws SQLException if database access error occurs
     * @throws ClassNotFoundException if JDBC driver not found
     */
    boolean update(T entity) throws SQLException, ClassNotFoundException;
    
    /**
     * Delete entity by its primary key (soft delete)
     * @param id the primary key value
     * @return true if delete successful, false otherwise
     * @throws SQLException if database access error occurs
     * @throws ClassNotFoundException if JDBC driver not found
     */
    boolean delete(ID id) throws SQLException, ClassNotFoundException;
}