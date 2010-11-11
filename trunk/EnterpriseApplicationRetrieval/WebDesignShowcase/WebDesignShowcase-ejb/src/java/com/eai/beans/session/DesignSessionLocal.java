package com.eai.beans.session;

import com.eai.beans.entity.Designs;
import java.util.Collection;
import javax.ejb.Local;

/**
 *
 * @author Max
 */
@Local
public interface DesignSessionLocal {

	Collection<Designs> findDesignsByUserId(long userID);

	void addDesign(long userId, String title, String url, String screenshot);
    
}
