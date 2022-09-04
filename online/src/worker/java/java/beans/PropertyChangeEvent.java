
package java.beans;

public interface PropertyChangeEvent
{
  public String getPropertyName();

  public Object getNewValue();

  public Object getSource();
}