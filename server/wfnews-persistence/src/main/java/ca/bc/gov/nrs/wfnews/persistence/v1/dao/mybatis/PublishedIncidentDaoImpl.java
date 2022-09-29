package ca.bc.gov.nrs.wfnews.persistence.v1.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ca.bc.gov.nrs.common.persistence.dao.DaoException;
import ca.bc.gov.nrs.common.persistence.dao.NotFoundDaoException;
import ca.bc.gov.nrs.wfnews.persistence.v1.dao.PublishedIncidentDao;
import ca.bc.gov.nrs.wfnews.persistence.v1.dao.mybatis.mapper.PublishedIncidentMapper;
import ca.bc.gov.nrs.wfnews.persistence.v1.dto.PagedDtos;
import ca.bc.gov.nrs.wfnews.persistence.v1.dto.PublishedIncidentDto;

@Repository
public class PublishedIncidentDaoImpl extends BaseDao implements
		PublishedIncidentDao {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(PublishedIncidentDaoImpl.class);
	
	public void setPublishedIncidentMapper(PublishedIncidentMapper publishedIncidentMapper) {
		this.publishedIncidentMapper = publishedIncidentMapper;
	}
	
	@Autowired
	private PublishedIncidentMapper publishedIncidentMapper;

	@Override
	public void insert(PublishedIncidentDto dto) throws DaoException {
		logger.debug("<insert");

		String publishedIncidentGuid = null;

		try {

			Map<String, Object> parameters = new HashMap<String, Object>();

			parameters.put("dto", dto);
			parameters.put("publishedIncidentDetailGuid", dto.getPublishedIncidentDetailGuid());
			int count = this.publishedIncidentMapper.insert(parameters);

			if(count==0) {
				throw new DaoException("Record not inserted: "+count);
			}
			
			publishedIncidentGuid = (String) parameters.get("publishedIncidentDetailGuid");
			
			dto.setPublishedIncidentDetailGuid(publishedIncidentGuid);
			
		} catch (RuntimeException e) {
			handleException(e);
		}

		logger.debug(">insert " + publishedIncidentGuid);
	}
	
	@Override
	public void update(PublishedIncidentDto dto) throws DaoException {
		logger.debug("<update");

		String publishedIncidentGuid = null;

		try {

			Map<String, Object> parameters = new HashMap<String, Object>();

			parameters.put("dto", dto);
			parameters.put("publishedIncidentDetailGuid", dto.getPublishedIncidentDetailGuid());
			int count = this.publishedIncidentMapper.update(parameters);

			if(count==0) {
				throw new DaoException("Record not inserted: "+count);
			}
			
			publishedIncidentGuid = (String) parameters.get("publishedIncidentDetailGuid");
			
			dto.setPublishedIncidentDetailGuid(publishedIncidentGuid);
			
		} catch (RuntimeException e) {
			handleException(e);
		}

		logger.debug(">update " + publishedIncidentGuid);
	}
	
	@Override
	public PublishedIncidentDto fetch(String publishedIncidentDetailGuid) throws DaoException {
		logger.debug("<fetch");

		PublishedIncidentDto result = null;

		try {

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("publishedIncidentDetailGuid", publishedIncidentDetailGuid);
			result = this.publishedIncidentMapper.fetch(parameters);

		} catch (RuntimeException e) {
			handleException(e);
		}

		logger.debug(">fetch " + result);
		return result;
	}
	
	@Override
	public PublishedIncidentDto fetchForIncidentGuid(String incidentGuid) throws DaoException {
		logger.debug("<fetch");

		PublishedIncidentDto result = null;

		try {

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("incidentGuid", incidentGuid);
			result = this.publishedIncidentMapper.fetchForIncidentGuid(parameters);

		} catch (RuntimeException e) {
			handleException(e);
		}

		logger.debug(">fetch " + result);
		return result;
	}
	
	@Override
	public void delete(String publishedIncidentDetailGuid, String userId) throws DaoException, NotFoundDaoException {
		logger.debug(">delete");
		
		try {

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("publishedIncidentDetailGuid", publishedIncidentDetailGuid);
			parameters.put("userId", userId);
			int count = this.publishedIncidentMapper.delete(parameters);

			if(count==0) {
				throw new DaoException("Record not deleted: "+count);
			}

		} catch (RuntimeException e) {
			handleException(e);
		}

		logger.debug("<delete");
		
	}
	
	@Override
	public PagedDtos<PublishedIncidentDto> select(Integer pageNumber, Integer pageRowCount) throws DaoException{
		
		PagedDtos<PublishedIncidentDto> results = new PagedDtos<>();
		
		
		try {

			Map<String, Object> parameters = new HashMap<String, Object>();
			Integer offset = null;
			int totalRowCount = this.publishedIncidentMapper.selectCount(parameters);
			pageNumber = pageNumber==null?Integer.valueOf(0):pageNumber;
			if(pageRowCount != null) { offset = Integer.valueOf((pageNumber.intValue()-1)*pageRowCount.intValue()); }
			//avoid jdbc exception for offset when pageNumber is 0
			if (offset != null && offset < 0) offset = 0;
			parameters.put("offset", offset);
			parameters.put("pageRowCount", pageRowCount);
			List<PublishedIncidentDto> dtos = this.publishedIncidentMapper.select(parameters);
			results.setResults(dtos);
			results.setPageRowCount(dtos.size());
			results.setTotalRowCount(totalRowCount);
			results.setPageNumber(pageNumber == null ? 0 : pageNumber.intValue());

		} catch (RuntimeException e) {
			handleException(e);
		}
		
		return results;

	}
	

}