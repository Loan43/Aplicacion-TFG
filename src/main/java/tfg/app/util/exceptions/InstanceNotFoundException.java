package tfg.app.util.exceptions;

@SuppressWarnings("serial")
public class InstanceNotFoundException extends Exception {

    private Object instanceId;
    private String instanceType;

    public InstanceNotFoundException(Object instanceId, String instanceType) {

        super("Instancia no encontrada (identificador = '" + instanceId + "' - tipo = '"
                + instanceType + "')");
        this.instanceId = instanceId;
        this.instanceType = instanceType;

    }

    public Object getInstanceId() {
        return instanceId;
    }

    public String getInstanceType() {
        return instanceType;
    }
}
