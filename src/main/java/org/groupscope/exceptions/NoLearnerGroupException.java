package org.groupscope.exceptions;

/**
 * @author Mykyta Liashko
 */
public class NoLearnerGroupException extends RuntimeException {

  public NoLearnerGroupException() {
    super("Learner or group is not created for user");
  }
}
