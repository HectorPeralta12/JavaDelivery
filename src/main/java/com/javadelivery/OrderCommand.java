package com.javadelivery;

/**
 * Command interface for order operations.
 * 
 * Design Pattern: Command
 * - Encapsulates a request as an object
 * - Allows parameterization and queuing of requests
 * - Supports undo operations
 */
public interface OrderCommand {

    /**
     * Executes the command.
     * 
     * @return true if execution was successful
     */
    boolean execute();

    /**
     * Undoes the command if possible.
     * 
     * @return true if undo was successful
     */
    boolean undo();

    /**
     * Returns a description of this command.
     */
    String getDescription();
}
