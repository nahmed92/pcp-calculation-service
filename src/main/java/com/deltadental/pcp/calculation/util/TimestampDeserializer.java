package com.deltadental.pcp.calculation.util;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.deltadental.pcp.calculation.error.PCPCalculationServiceErrors;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimestampDeserializer extends StdDeserializer<Timestamp> {

	private static final long serialVersionUID = 1L;

	public TimestampDeserializer() {
		this(null);
	}
	
	public TimestampDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Timestamp deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JacksonException {
		String dateStr = jsonParser.getText();
		log.info("Parsing timestamp {} ",dateStr);
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSXXX");
			Date date = format.parse(dateStr);
			Timestamp ts = new Timestamp(date.getTime());
			log.info("Parsed timestamp {} ",dateStr);
			return ts;
		} catch (Exception e) {
			log.error("Unable to parse timestamp {} ",dateStr);
			throw PCPCalculationServiceErrors.INVALID_TIMESTAMP_ERROR.createException(e);
		}
	}
}
