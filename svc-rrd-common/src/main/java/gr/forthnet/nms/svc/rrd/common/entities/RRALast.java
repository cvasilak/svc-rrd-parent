package gr.forthnet.nms.svc.rrd.common.entities;

public class RRALast {
	private String name;
	private double value;

	public RRALast() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
