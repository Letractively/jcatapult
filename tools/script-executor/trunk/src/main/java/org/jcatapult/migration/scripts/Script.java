package org.jcatapult.migration.scripts;

/**
 * Represents a Script.  Script languages should implement this interface
 *
 * @author jhumphrey
 */
public interface Script {

    /**
     * Executes the script
     *
     * @param scriptContext the script context
     */
    void execute(ScriptContext scriptContext);
}
