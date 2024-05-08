package com.hansung.roadbuddyandroid

import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Response(
    val data: Data
)

data class Data(
    @SerializedName("geocoded_waypoints")
    val geocodedWaypoints: List<GeocodedWaypoint>,
    val routes: List<Route>
)
data class GeocodedWaypoint(
    @SerializedName("geocoder_status")
    val geocoderStatus: String,
    @SerializedName("place_id")
    val placeId: String,
    val types: List<String>
)
@Parcelize
data class Route(
    val bounds: Bounds,
    val copyrights: String,
    val legs: List<Leg>
) : Parcelable
@Parcelize
data class Bounds(
    val northeast: Location,
    val southwest: Location
) : Parcelable
@Parcelize
data class Location(
    val lat: Double,
    val lng: Double
) : Parcelable
@Parcelize
data class Leg(
    @SerializedName("arrival_time")
    val arrivalTime: TimeInfo,
    @SerializedName("departure_time")
    val departureTime: TimeInfo,
    val distance: Distance,
    val duration: Duration,
    @SerializedName("end_address")
    val endAddress: String,
    @SerializedName("end_location")
    val endLocation: Location,
    @SerializedName("start_address")
    val startAddress: String,
    @SerializedName("start_location")
    val startLocation: Location,
    val steps: List<Step>
) : Parcelable

@Parcelize
data class TimeInfo(
    val text: String,
    @SerializedName("time_zone")
    val timeZone: String,
    val value: Long
) : Parcelable
@Parcelize
data class Distance(
    val text: String,
    val value: Int
) : Parcelable

@Parcelize
data class Duration(
    val text: String,
    val value: Int
) : Parcelable
@Parcelize
data class Step(
    val distance: Distance,
    val duration: Duration,
    @SerializedName("end_location")
    val endLocation: Location,
    @SerializedName("html_instructions")
    val htmlInstructions: String?,
    val polyline: Polyline,
    @SerializedName("start_location")
    val startLocation: Location,
    @SerializedName("transit_details")
    val transitDetails: TransitDetails?,
    @SerializedName("travel_mode")
    val travelMode: String,
    val maneuver: String?,
    val steps: List<Step>?
) : Parcelable
@Parcelize
data class Polyline(
    val points: String
) : Parcelable

@Parcelize
data class TransitDetails(
    @SerializedName("arrival_stop")
    val arrivalStop: TransitStop,
    @SerializedName("arrival_time")
    val arrivalTime: TimeInfo,
    @SerializedName("departure_stop")
    val departureStop: TransitStop,
    @SerializedName("departure_time")
    val departureTime: TimeInfo,
    val headsign: String,
    val line: Line,
    val numStops: Int
) : Parcelable

@Parcelize
data class TransitStop(
    val location: Location,
    val name: String
) : Parcelable

@Parcelize
data class Line(
    val agencies: List<Agency>,
    val color: String,
    val name: String,
    @SerializedName("short_name")
    val shortName: String,
    @SerializedName("text_color")
    val textColor: String,
    val vehicle: Vehicle
) : Parcelable

@Parcelize
data class Agency(
    val name: String,
    val url: String
) : Parcelable

@Parcelize
data class Vehicle(
    val icon: String,
    val name: String,
    val type: String
) : Parcelable