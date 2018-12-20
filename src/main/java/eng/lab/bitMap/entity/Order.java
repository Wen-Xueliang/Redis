package eng.lab.bitMap.entity;

import lombok.Data;

import java.util.Date;


@Data
public class Order {
    private String id;
    private String userId;
    private String tradeId;
    private Date createDate;

    public Order() {

    }

    public Order(String id, String userId, String tradeId, Date createDate) {
        this.id = id;
        this.userId = userId;
        this.tradeId = tradeId;
        this.createDate = createDate;
    }
}
