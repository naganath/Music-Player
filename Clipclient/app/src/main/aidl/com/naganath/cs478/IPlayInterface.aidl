// IPlayInterface.aidl
package com.naganath.cs478;

// Declare any non-default types here with import statements
// IPlayInterface.aidl
// Declare any non-default types here with import statements

interface IPlayInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void play(int index);
    void pause();
    void resume();
    void stop();
    String[] getAll();

}
