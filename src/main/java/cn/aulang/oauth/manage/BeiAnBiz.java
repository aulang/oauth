package cn.aulang.oauth.manage;

import cn.aulang.oauth.entity.BeiAn;
import cn.aulang.oauth.entity.BeiAnEntry;
import cn.aulang.oauth.repository.BeiAnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2020-05-02 12:01
 */
@Service
public class BeiAnBiz {

    private final BeiAnRepository dao;

    private BeiAn beiAn = null;

    @Autowired
    public BeiAnBiz(BeiAnRepository dao) {
        this.dao = dao;
    }

    public BeiAn save(BeiAn entity) {
        return (beiAn = dao.save(entity));
    }

    public BeiAn get() {
        if (beiAn != null) {
            return beiAn;
        }

        beiAn = dao.findFirstBy();

        if (beiAn == null) {
            beiAn = new BeiAn();
        }

        if (beiAn.getMiit() == null) {
            beiAn.setMiit(new BeiAnEntry());
        }

        if (beiAn.getMps() == null) {
            beiAn.setMps(new BeiAnEntry());
        }

        return beiAn;
    }
}
