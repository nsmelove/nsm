package com.nsm.mvc.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description for this file
 *
 * @author Created by nsm on 2018/6/11.
 */
@RestControllerAdvice
@RequestMapping("/product")
public class ProductController extends ErrorHandler{

    @RequestMapping("/category/list")
    @ResponseBody
    public List<Object> categoryList(@RequestAttribute long uid, @RequestAttribute String sid){
        //TODO
        return null;
    }

    @RequestMapping("/category/add")
    @ResponseBody
    public long addCategory(@RequestAttribute long uid, @RequestAttribute String sid,
                                    @RequestParam long categoryName){
        //TODO
        return 1;
    }

    @RequestMapping("/category/{catId}/delete")
    @ResponseBody
    public long deleteCategory(@RequestAttribute long uid, @RequestAttribute String sid,
                               @PathVariable long catId){
        //TODO
        return 1;
    }

    @RequestMapping("/property/list")
    @ResponseBody
    public List<Object> propertyList(@RequestAttribute long uid, @RequestAttribute String sid){
        //TODO
        return null;
    }

    @RequestMapping("/property/add")
    @ResponseBody
    public long addProperty(@RequestAttribute long uid, @RequestAttribute String sid,
                            @RequestParam long propertyName, @RequestParam long valueType){
        //TODO
        return 1;
    }

    @RequestMapping("/property/{proId}/delete")
    @ResponseBody
    public long deleteProperty(@RequestAttribute long uid, @RequestAttribute String sid,
                            @PathVariable long proId){
        //TODO
        return 1;
    }

    @RequestMapping("/list")
    @ResponseBody
    public List<Object> productList(@RequestAttribute long uid, @RequestAttribute String sid,
                                    @RequestParam(required = false) long categoryId){
        //TODO
        return null;
    }

    @RequestMapping("/add")
    @ResponseBody
    public long addProduct(@RequestAttribute long uid, @RequestAttribute String sid,
                                    @RequestBody Object product){
        //TODO
        return 1;
    }
}
