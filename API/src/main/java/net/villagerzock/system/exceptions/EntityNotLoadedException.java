package net.villagerzock.system.exceptions;

public class EntityNotLoadedException extends RuntimeException {
  public EntityNotLoadedException(int entity) {
    super("Entity #" + entity + " has been requested, but is not loaded");
  }
}
