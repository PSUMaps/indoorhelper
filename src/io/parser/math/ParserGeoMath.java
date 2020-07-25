// License: GPL. For details, see LICENSE file.
package io.parser.math;

import org.openstreetmap.josm.data.coor.LatLon;

import io.parser.BIMtoOSMParser.IFCUnit;
import io.parser.data.Point3D;

/**
 * Class providing math functions for geodetic operations
 * @author rebsc
 *
 */
public class ParserGeoMath {

	private static double R = 6371e3;	// earth radius in meter

	/**
	 * Method transforms cartesian point to latlon point with given latlon origin coordinate (latlon for cartesian 0.0/0.0)
	 * and cartesian unit like m or cm
	 * @param cartesianPoint to translate to latlon
	 * @param cartesianOrigin cartesian representation of latLonOfCartesianOrigin
	 * @param latLonOfCartesianOrigin latlon of cartesian origin (0.0/0.0)
	 * @param cartesianUnit m or cm
	 * @return latlon of cartesian point
	 */
	public static LatLon cartesianToGeodetic(Point3D cartesianPoint, Point3D cartesianOrigin,LatLon latLonOfCartesianOrigin, IFCUnit cartesianUnit) {
		double originCartX = cartesianOrigin.getX();
		double originCartY = cartesianOrigin.getY();
		double originLat = degToRad(latLonOfCartesianOrigin.lat());
		double originLon = degToRad(latLonOfCartesianOrigin.lon());
		double pointX = cartesianPoint.getX();
		double pointY = cartesianPoint.getY();

		// get bearing
	    double bearing = Math.atan2(pointY - originCartY, pointX - originCartX);
		bearing = Math.toRadians(90.0) - bearing;

		// get distance
		if(cartesianUnit.equals(IFCUnit.cm)) {
			pointX /= 100.0;
			pointY /= 100.0;
		}
		else if(cartesianUnit.equals(IFCUnit.mm)) {
			pointX /= 1000.0;
			pointY /= 1000.0;
		}
		double d = Math.sqrt(Math.pow((pointX - originCartX),2) + Math.pow((pointY - originCartY),2));

		double pointLat = Math.asin(
				Math.sin(originLat)*Math.cos(d/R) +
				Math.cos(originLat) * Math.sin(d/R) * Math.cos(bearing));
		double pointLon = originLon +
				Math.atan2(
				Math.sin(bearing) * Math.sin(d/R) * Math.cos(originLat),
				Math.cos(d/R)-Math.sin(originLat)*Math.sin(pointLat));

		return new LatLon(radToDeg(pointLat) , radToDeg(pointLon));
	}

	public static double degreeMinutesSecondsToLatLon(double degrees, double minutes, double seconds) {
		return degrees + (minutes/60.0) + (seconds/3600.0);
	}

	private static double degToRad(double x){
        return x * Math.PI / 180.0;
    }

	private static double radToDeg(double x) {
		return x /(Math.PI/180.0);
	}

}
