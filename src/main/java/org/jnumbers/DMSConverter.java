/* 
 Copyright (c) 2015, Laurent Bourges. All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 - Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 - Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jnumbers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic DMS converter
 *
 * @author Laurent Bourges
 */
public final class DMSConverter {

	/** Logger */
	private static final Logger _logger = LoggerFactory.getLogger(DMSConverter.class.getName());
	/** Specify the value of one arcminute in degrees */
	public static final double DEG_IN_ARCMIN = 60d;
	/** Specify the value of one arcminute in degrees */
	public static final double ARCMIN_IN_DEGREES = (1d / 60d);
	/** Specify the value of one arcsecond in degrees */
	public static final double ARCSEC_IN_DEGREES = (1d / 3600d);
	/** Specify the value of one arcsecond in degrees */
	public static final double DEG_IN_ARCSEC = 3600d;
	/** Specify the value of one milli arcsecond in degrees */
	public static final double MILLI_ARCSEC_IN_DEGREES = (1d / 3600000d);
	/** Specify the value of one arcminute in arcsecond */
	public static final double ARCMIN_IN_ARCSEC = 60d;
	/** Specify the value of one hour in degrees */
	public static final double HOUR_IN_DEGREES = 360d / 24d;
	/** Specify the value of one hour in degrees */
	public static final double DEG_IN_HOUR = 24d / 360d;
	/** Specify the value of one minute in degrees */
	public static final double MIN_IN_DEG = 15d / 60d;
	/** Specify the value of one degree in minute */
	public static final double DEG_IN_MIN = 60d / 15d;
	/** Specify the value of one hour in minute */
	public static final double HOUR_IN_MIN = 60d;
	/** threshold for rounding millis (truncating) */
	public static final double MILLIS_ROUND_THRESHOLD = 0.5e-3d;

	private DMSConverter() {
		// utility class
	}

	/**
	 * Convert the given HMS value.
	 *
	 * @param hms the value as a HH:MM:SS.TT or HH MM SS.TT string.
	 *
	 * @return the angle in degrees or NaN if invalid value
	 */
	public static double parseHMS(final String hms) {

		// HMS can be given as HH:MM:SS.TT or HH MM SS.TT. 
		// Replace ':' by ' ', and remove trailing and leading space
		final String input = hms.replace(':', ' ').trim();

		double hh = 0d;
		double hm = 0d;
		double hs = 0d;

		// Parse the given string
		try {
			final String[] tokens = input.split(" ");

			final int len = tokens.length;

			if (len > 0) {
				hh = Double.parseDouble(tokens[0]);
			}
			if (len > 1) {
				hm = Double.parseDouble(tokens[1]);
			}
			if (len > 2) {
				hs = Double.parseDouble(tokens[2]);
			}

		} catch (NumberFormatException nfe) {
			_logger.debug("format exception: ", nfe);
			hh = hm = hs = Double.NaN;
		}

		// Get sign of hh which has to be propagated to hm and hs
		final double sign = (input.startsWith("-")) ? -1d : 1d;

		// Convert to degrees
		// note : hh already includes the sign :
		final double angle = (hh + sign * (hm * ARCMIN_IN_DEGREES + hs * ARCSEC_IN_DEGREES)) * HOUR_IN_DEGREES;

		if (_logger.isDebugEnabled()) {
			_logger.debug("HMS : ’" + hms + "' = '" + angle + "'.");
		}

		return angle;
	}

	/**
	 * Convert the given DMS value.
	 *
	 * @param dms the value as a DD:MM:SS.TT or DD MM SS.TT string.
	 *
	 * @return the angle as a double in degrees or NaN if invalid value
	 */
	public static double parseDMS_REF(final String dms) {

		// DMS can be given as DD:MM:SS.TT or DD MM SS.TT. 
		// Replace ':' by ' ', and remove trailing and leading space
		final String input = dms.replace(':', ' ').trim();

		double dd = 0d;
		double dm = 0d;
		double ds = 0d;

		// Parse the given string
		try {
			final String[] tokens = input.split(" ");

			final int len = tokens.length;

			if (len > 0) {
				dd = Double.parseDouble(tokens[0]);
			}
			if (len > 1) {
				dm = Double.parseDouble(tokens[1]);
			}
			if (len > 2) {
				ds = Double.parseDouble(tokens[2]);
			}

		} catch (NumberFormatException nfe) {
			_logger.debug("format exception: ", nfe);
			dd = dm = ds = Double.NaN;
		}

		// Get sign of dd which has to be propagated to dm and ds
		final double sign = (input.startsWith("-")) ? -1d : 1d;

		// Convert to degrees
		// note : dd already includes the sign :
		final double angle = dd + sign * (dm * ARCMIN_IN_DEGREES + ds * ARCSEC_IN_DEGREES);

		if (_logger.isDebugEnabled()) {
			_logger.debug("DMS : ’" + dms + "' = '" + angle + "'.");
		}

		return angle;
	}

	/**
	 * Convert the given DMS value.
	 *
	 * @param dms the value as a DD:MM:SS.TT string.
	 *
	 * @return the angle as a double in degrees or NaN if invalid value
	 */
	public static double parseDMS_OLD(final CharSequence dms) {
		return parseDMS_OLD(dms, ':');
	}

	/**
	 * Convert the given DMS value.
	 *
	 * @param dms the value as a DD<sep>MM<sep>SS.TT string.
	 * @param sep separator character
	 *
	 * @return the angle as a double in degrees or NaN if invalid value
	 */
	public static double parseDMS_OLD(final CharSequence dms, final char sep) {

		double dd, dm, ds;

		// Parse the given string:
		try {
			final int length = dms.length();
			int pos1 = NumberParser.indexOf(dms, sep, 0, length);

			if (pos1 == -1) {
				// no separator: 1st value as double ?
				dd = NumberParser.getDouble(dms);
				dm = ds = 0d;
			} else {
				dd = NumberParser.getDouble(dms, 0, pos1);
				pos1++;

				int pos2 = NumberParser.indexOf(dms, sep, pos1, length);

				if (pos2 == -1) {
					// no separator: 2th value as double ?
					dm = NumberParser.getDouble(dms, pos1, length);
					ds = 0d;
				} else {
					dm = NumberParser.getDouble(dms, pos1, pos2);
					pos2++;

					// 3rd value as double ?
					ds = NumberParser.getDouble(dms, pos2, length);
				}
			}

		} catch (NumberFormatException nfe) {
			_logger.debug("format exception: ", nfe);
			dd = dm = ds = Double.NaN;
		}

		// Get sign of dd which has to be propagated to dm and ds
		final double sign = (dms.charAt(0) == '-') ? -1d : 1d;

		// Convert to degrees
		// note : dd already includes the sign :
		final double angle = dd + sign * (dm * ARCMIN_IN_DEGREES + ds * ARCSEC_IN_DEGREES);

		if (_logger.isDebugEnabled()) {
			_logger.debug("DMS : ’" + dms + "' = '" + angle + "'.");
		}

		return angle;
	}

	/**
	 * Convert the given DMS value.
	 *
	 * @param dms the value as a DD:MM:SS.TT string.
	 *
	 * @return the angle as a double in degrees or NaN if invalid value
	 */
	public static double parseDMS(final CharSequence dms) {
		return DMSConverter.parseDMS(dms, ':');
	}

	/**
	 * Convert the given DMS value.
	 *
	 * @param dms the value as a [+/-]DDD<sep>MM<sep>SS.TT string.
	 * @param sep separator character
	 *
	 * @return the angle as a double in degrees or NaN if invalid value
	 */
	public static double parseDMS(final CharSequence dms, final char sep) {

		double dd, dm, ds;

		// Parse the given string:
		try {
			final int length = dms.length();
			int pos1 = NumberParser.indexOf(dms, sep, 0, length);

			if (pos1 == -1) {
				// no separator: 1st value as double ?
				dd = NumberParser.getDouble(dms);
				dm = ds = 0d;
			} else {
				dd = NumberParser.getIntegerUnsafeAsDouble(dms, 0, pos1);
				pos1++;

				int pos2 = NumberParser.indexOf(dms, sep, pos1, length);

				if (pos2 == -1) {
					// no separator: 2th value as double ?
					dm = NumberParser.getDouble(dms, pos1, length);
					ds = 0d;
				} else {
					dm = NumberParser.getPositiveIntegerUnsafeAsDouble(dms, pos1, pos2);
					pos2++;

					// 3rd value as double ?
					ds = NumberParser.getDouble(dms, pos2, length);
				}
			}

		} catch (NumberFormatException nfe) {
			_logger.debug("format exception: ", nfe);
			dd = dm = ds = Double.NaN;
		}

		// Get sign of dd which has to be propagated to dm and ds
		final double sign = (dms.charAt(0) == '-') ? -1d : 1d;

		// Convert to degrees
		// note : dd already includes the sign :
		final double angle = dd + sign * (dm * ARCMIN_IN_DEGREES + ds * ARCSEC_IN_DEGREES);

		if (_logger.isDebugEnabled()) {
			_logger.debug("DMS : ’" + dms + "' = '" + angle + "'.");
		}

		return angle;
	}

	/**
	 * Return the DMS format of the given angle
	 * 
	 * Warning: this method creates a new StringBuilder(16) for each invocation; 
	 * use toDMS(StringBuilder, double) instead to use a given StringBuilder instance
	 *
	 * @param angle angle in degrees within range [-90; 90]
	 * @return string DMS representation
	 */
	public static String toDMS(final double angle) {
		return toDMS(new StringBuilder(16), angle).toString();
	}

	/**
	 * Return the HMS format of the given angle
	 * 
	 * Warning: this method creates a new StringBuilder(16) for each invocation; 
	 * use toHMS(StringBuilder, double) instead to use a given StringBuilder instance
	 *
	 * @param angle angle in degrees > -360.0
	 * @return string HMS representation, null otherwise
	 */
	public static String toHMS(final double angle) {
		return toHMS(new StringBuilder(16), angle).toString();
	}

	/**
	 * Append the DMS format of the given angle to given string builder
	 * @param sb string builder to append into
	 * @param angle angle in degrees within range [-360; 360]
	 * @return given string builder
	 */
	public static StringBuilder toDMS(final StringBuilder sb, final double angle) {
		return toDMS(sb, angle, 360d);
	}

	/**
	 * Append the DMS format of the given angle to given string builder
	 * @param sb string builder to append into
	 * @param angle angle in degrees within range [-maxValue; maxValue]
	 * @param maxValue maximum angle value in degrees
	 * @return given string builder
	 */
	public static StringBuilder toDMS(final StringBuilder sb, final double angle, final double maxValue) {
		final boolean negative;
		final double absAngle;
		if (angle < 0.0D) {
			negative = true;
			absAngle = -angle;
		} else {
			negative = false;
			absAngle = angle;
		}
		/* check boundaries */
		if (absAngle > maxValue) {
			return sb.append("~");
		}
		/* print deg field */
		final int iDeg = (int) Math.floor(absAngle);
		final double remainder = absAngle - iDeg;

		/* always print sign '+' as DEC is typically within range [-90; 90] */
		sb.append((negative) ? '-' : '+');
		if (iDeg < 10) {
			sb.append('0');
		}
		sb.append(iDeg);

		return toMS(sb, remainder);
	}

	/**
	 * Append the HMS format of the given angle to given string builder
	 * @param sb string builder to append into
	 * @param angle angle in degrees > -360.0
	 * @return given string builder
	 */
	public static StringBuilder toHMS(final StringBuilder sb, final double angle) {
		final boolean negative;
		final double absAngle;
		if (angle < 0.0D) {
			negative = true;
			/* convert deg in hours */
			absAngle = -angle * DEG_IN_HOUR;
		} else {
			negative = false;
			/* convert deg in hours */
			absAngle = angle * DEG_IN_HOUR;
		}
		/* check boundaries */
		if (absAngle > 24d) {
			return sb.append("~");
		}
		/* print hour field */
		final int iHour = (int) Math.floor(absAngle);
		final double remainder = absAngle - iHour;

		/* avoid '+' for positive values as RA is typically within range [0.0; 24.0[ */
		if (negative) {
			sb.append('-');
		}
		if (iHour < 10) {
			sb.append('0');
		}
		sb.append(iHour);

		return toMS(sb, remainder);
	}

	private static StringBuilder toMS(final StringBuilder sb, final double angle) {
		final double fMinute = DEG_IN_ARCMIN * angle;
		final int iMinute = (int) Math.floor(fMinute);

		final double fSecond = ARCMIN_IN_ARCSEC * (fMinute - iMinute);
		final int iSecond = (int) Math.floor(fSecond);

		double remainder = fSecond - iSecond;

		// fix last digit by 2 ULP to fix 0.5 rounding
		remainder += 2.0 * Math.ulp(remainder);

		/* print min field */
		sb.append(':');
		if (iMinute < 10) {
			sb.append('0');
		}
		sb.append(iMinute);

		/* print min field */
		sb.append(':');
		if (iSecond < 10) {
			sb.append('0');
		}
		sb.append(iSecond);

		if (remainder >= MILLIS_ROUND_THRESHOLD) {
			final int iMillis = (int) Math.round(1e3d * remainder);
			sb.append('.');
			if (iMillis < 100) {
				sb.append('0');
			}
			if (iMillis < 10) {
				sb.append('0');
			}
			sb.append(iMillis);
		}
		return sb;
	}
}
