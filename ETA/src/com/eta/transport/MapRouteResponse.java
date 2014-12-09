package com.eta.transport;

import java.util.List;

public class MapRouteResponse {
	public List<Route>routes;
	public Status status;
}

enum Status {
	OK,
	NOT_FOUND,
	ZERO_RESULTS,
	MAX_WAYPOINTS_EXCEEDED,
	INVALID_REQUEST,
	OVER_QUERY_LIMIT,
	REQUEST_DENIED,
	UNKNOWN_ERROR;
}
class Route {
	public String summary;
	public List<Leg> legs;
	public List<Integer> waypoint_order;
	public PolyLine overview_polyline;
}

class Leg {
	
}
class Bounds {
	
}
class LatLng {
	public Double lat;
	public Double lng;
}
class PolyLine {
	String points;
}
class Distance {
	public String text;
	public int value;
	
	public Distance(){
		//For Gson
	}
	public Distance(String text, int value) {
		super();
		this.text = text;
		this.value = value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "Distance [text=" + text + ", value=" + value + "]";
	}
}
class Duration {
	public String text;
	//In Seconds
	public int value; 
	
	public Duration(){
		//For Gson
	}

	public Duration(String text, int value) {
		super();
		this.text = text;
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Duration [text=" + text + ", value=" + value + "]";
	}
}

