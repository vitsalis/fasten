package eu.fasten.analyzer.javacgopal.exceptions;

public class ExceededMaxCallGraphSize extends Exception {

    public ExceededMaxCallGraphSize(String errorMessage) {
        super(errorMessage);
    }
}
