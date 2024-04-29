package com.hansung.roadbuddyandroid

data class Response(
    val data: Data
)

data class Data(
    val geocodedWaypoints: List<GeocodedWaypoint>,
val routes: List<Route>
)

data class GeocodedWaypoint(
    val geocoderStatus: String,
    val placeId: String,
    val types: List<String>
)

data class Route(
    val bounds: Bounds,
    val copyrights: String,
    val legs: List<Leg>
)

data class Bounds(
    val northeast: Location,
    val southwest: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class Leg(
    val arrivalTime: TimeInfo,
    val departureTime: TimeInfo,
    val distance: Distance,
    val duration: Duration,
    val endAddress: String,
    val endLocation: Location,
    val startAddress: String,
    val startLocation: Location,
    val steps: List<Step>
)

data class TimeInfo(
    val text: String,
    val timeZone: String,
    val value: Long
)

data class Distance(
    val text: String,
    val value: Int
)

data class Duration(
    val text: String,
    val value: Int
)

data class Step(
    val distance: Distance,
    val duration: Duration,
    val endLocation: Location,
    val htmlInstructions: String?,
    val polyline: Polyline,
    val startLocation: Location,
    val transitDetails: TransitDetails?,
    val travelMode: String,
    val maneuver: String?,
    val steps: List<Step>?
)

data class Polyline(
    val points: String
)

data class TransitDetails(
    val arrivalStop: TransitStop,
    val arrivalTime: TimeInfo,
    val departureStop: TransitStop,
    val departureTime: TimeInfo,
    val headsign: String,
    val line: Line,
    val numStops: Int
)

data class TransitStop(
    val location: Location,
    val name: String
)

data class Line(
    val agencies: List<Agency>,
val color: String,
val name: String,
val shortName: String,
val textColor: String,
val vehicle: Vehicle
)

data class Agency(
    val name: String,
    val url: String
)

data class Vehicle(
    val icon: String,
    val name: String,
    val type: String
)