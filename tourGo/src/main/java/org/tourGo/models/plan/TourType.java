package org.tourGo.models.plan;

public enum TourType {
나홀로여행("혼자"),
커플여행("연인"),
친구여행("친구"),
부모님여행("부모님"),
가족여행("가족");

private final String name;

TourType(String name) {
	this.name=name;
}
public String getName() {
	return name;
}
}
