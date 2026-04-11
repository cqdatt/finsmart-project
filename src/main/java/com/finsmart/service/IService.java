package com.finsmart.service;

import java.sql.SQLException;
import java.util.List;

/**
 * Simplified base interface for Service classes
 */
public interface IService<T, ID> {
    
    T getById(ID id) throws SQLException, ClassNotFoundException;
    
    List<T> getAll() throws SQLException, ClassNotFoundException;
    
    boolean delete(ID id) throws SQLException, ClassNotFoundException, Exception;
}