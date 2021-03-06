package com.zxkj.goods.controller;

import com.zxkj.cart.model.Cart;
import com.zxkj.common.exception.BusinessException;
import com.zxkj.common.web.RespResult;
import com.zxkj.goods.feign.SkuFeign;
import com.zxkj.goods.model.Sku;
import com.zxkj.goods.service.SKuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SkuController implements SkuFeign {

    @Autowired
    private SKuService sKuService;

    /***
     * 库存递减
     */
    public RespResult<Integer> dcount(@RequestBody List<Cart> carts) {
        return RespResult.ok(sKuService.dcount(carts));
    }

    /***
     * 根据ID查询商品详情
     * @return
     */
    public RespResult<Sku> one(@PathVariable(value = "id") String id) {
        Sku sku = sKuService.getById(id);
        return RespResult.ok(sku);
    }

    /****
     * 根据推广分类查询推广产品列表
     *
     */
    public RespResult<List<Sku>> typeItems(@RequestParam(value = "id") Integer id) {
        //查询
        List<Sku> skus = sKuService.typeSkuItems(id);
        return RespResult.ok(skus);
    }

    /****
     * 根据推广分类查询推广产品列表
     */
    public RespResult delTypeItems(@RequestParam(value = "id") Integer id) {
        sKuService.delTypeSkuItems(id);
        return RespResult.ok();
    }

    /****
     * 根据推广分类查询推广产品列表
     *
     */
    public RespResult updateTypeItems(@RequestParam(value = "id") Integer id) {
        //修改
        sKuService.updateTypeSkuItems(id);
        return RespResult.ok();
    }

}
