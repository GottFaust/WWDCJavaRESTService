package org.gottfaust.wwdc.controller.interfaces;


import java.util.concurrent.*;

public interface IService {

    /** The Executor for handling threading **/
    ExecutorService executor = Executors.newFixedThreadPool(10);

    /** The pool of threads handled by the Executor **/
    CompletionService pool = new ExecutorCompletionService(executor);
}
