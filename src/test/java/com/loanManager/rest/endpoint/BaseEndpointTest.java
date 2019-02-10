package com.loanManager.rest.endpoint;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanManager.enums.TermUnit;
import com.loanManager.rest.domain.Loan;

/**
 * Abstract Test with common test methods.
 * 
 * @author Rados³aw Ba³trukiewicz
 * 
 */
public abstract class BaseEndpointTest {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected static final MediaType JSON_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("UTF-8"));
	

	@Autowired
    protected WebApplicationContext webApplicationContext;

	@Autowired
	ObjectMapper objectMapper;
	
	protected MockMvc mockMvc;

    protected void setup() throws Exception {

    	this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }
    
	/**
	 * For returning JSON representation of an object
	 * @param object instance
	 * @return json string
	 * @throws IOException
	 */
	protected String json(Object o) throws IOException {

		return objectMapper.writeValueAsString(o);
	}
	/**
	 * For returning serialized  loan objects from json instances
	 * @param  json string
	 * @param  returned java class
	 * @return java class object
	 * @throws IOException
	 * @throws JSONException 
	 * @throws ParseException 
	 */
	protected Loan convertJSONStringToObject(String json) throws IOException, JSONException, ParseException {
		JSONObject jsonObject = new JSONObject(json);		
		BigDecimal amount = new BigDecimal(jsonObject.getString("amount"));
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(jsonObject.getString("dueDate").substring(0,19), dateFormatter);
		Instant instant = dateTime.toInstant(OffsetDateTime.now().getOffset());
		Date date = Date.from(instant);     
		BigDecimal cost = new BigDecimal( jsonObject.getString("cost"));
		Long term = new Long(jsonObject.getString("term"));
		Loan loan = new Loan(term,amount);
		loan.setCost(cost);
		loan.setDueDate(date);
		loan.setTermUnit(TermUnit.valueOf(jsonObject.getString("termUnit")));
	    return loan;
	}

}
