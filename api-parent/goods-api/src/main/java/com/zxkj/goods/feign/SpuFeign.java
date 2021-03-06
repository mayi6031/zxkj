package com.zxkj.goods.feign;

import com.zxkj.common.constant.ServiceIdConstant;
import com.zxkj.common.web.RespResult;
import com.zxkj.goods.model.Product;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/*****
 * @Author:
 * @Description:
 ****/
@FeignClient(value = ServiceIdConstant.GOODS_SERVICE, path = "/", fallbackFactory = SpuFeignFallback.class)    //服务名字
public interface SpuFeign {

    /***
     * 查询Product
     */
    @GetMapping(value = "/spu/product/{id}")
    RespResult<Product> one(@PathVariable(value = "id") String id);
}


@Component
class SpuFeignFallback implements SpuFeign, FallbackFactory<SpuFeign> {
    private static final Logger logger = LoggerFactory.getLogger(com.zxkj.goods.feign.SpuFeignFallback.class);
    private Throwable throwable;

    @Override
    public SpuFeign create(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    @Override
    public RespResult<Product> one(String id) {
        logger.error("SpuFeignFallback -> one错误信息：{}", throwable.getMessage());
        return RespResult.error(throwable.getMessage());
    }


}