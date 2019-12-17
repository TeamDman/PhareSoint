package ca.teamdman.pharesoint.payloads;

public class Payload {
	public final String author;
	public final String content;
	public final String route;
	public Payload(String author, String route, String content) {
		this.author = author;
		this.route = route;
		this.content = content;
	}
}
