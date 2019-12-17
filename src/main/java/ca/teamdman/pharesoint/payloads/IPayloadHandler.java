package ca.teamdman.pharesoint.payloads;

public interface IPayloadHandler {
	public String getName();
	public void handle(Payload payload);
}
