/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.controller.mall;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.NewBeeMallException;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallGoodsDetailVO;
import ltd.newbee.mall.controller.vo.SearchPageCategoryVO;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.service.NewBeeMallCategoryService;
import ltd.newbee.mall.service.NewBeeMallGoodsService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.PageQueryUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

@Controller
public class GoodsController {

	@Resource
	private NewBeeMallGoodsService newBeeMallGoodsService;
	@Resource
	private NewBeeMallCategoryService newBeeMallCategoryService;

	@GetMapping({ "/search", "/search.html" })
	public String searchPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {

		if (StringUtils.isEmpty(params.get("page"))) {
			params.put("page", 1);
		}
		// 常量类里默认十条
		params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
		// 封装分类数据 分类id不为空的话查询分类数据
		if (params.containsKey("goodsCategoryId") && !StringUtils.isEmpty(params.get("goodsCategoryId") + "")) {
			Long categoryId = Long.valueOf(params.get("goodsCategoryId") + "");
			SearchPageCategoryVO searchPageCategoryVO = newBeeMallCategoryService.getCategoriesForSearch(categoryId);
			if (searchPageCategoryVO != null) {
				request.setAttribute("goodsCategoryId", categoryId);
				request.setAttribute("searchPageCategoryVO", searchPageCategoryVO);

			}
		}
		// 封装参数供前端回显 orderBy 新品 价格等
		if (params.containsKey("orderBy") && !StringUtils.isEmpty(params.get("orderBy") + "")) {
			request.setAttribute("orderBy", params.get("orderBy") + "");
		}
		String keyword = "";
		// 对keyword做过滤 去掉空格
		if (params.containsKey("keyword") && !StringUtils.isEmpty((params.get("keyword") + "").trim())) {
			keyword = params.get("keyword") + "";
		}
		// 回显关键字
		request.setAttribute("keyword", keyword);
		params.put("keyword", keyword);
		// 搜索上架状态下的商品
		params.put("goodsSellStatus", Constants.SELL_STATUS_UP);
		PageQueryUtil pageUtil = new PageQueryUtil(params);
		request.setAttribute("pageResult", newBeeMallGoodsService.searchNewBeeMallGoods(pageUtil));
		return "mall/search";
	}

	// 2021/04/06 手机分类排序 排序 searchSecondLevel方法
	@GetMapping({ "/secondLevelCategory", "/search.html" })
	public String searchCommonPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
		if (StringUtils.isEmpty(params.get("page"))) {
			params.put("page", 1);
		}
		params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);

		// 搜索上架状态下的商品
		params.put("goodsSellStatus", Constants.SELL_STATUS_UP);
		// 封装商品数据 PageQueryUtil（map对象）
		PageQueryUtil pageUtil = new PageQueryUtil(params);
		// 执行查询语句 searchNewBeeMallGoods
		request.setAttribute("pageResult", newBeeMallGoodsService.searchSecondLevel(pageUtil));

		return "mall/search";
	}

	@GetMapping("/goods/detail/{goodsId}")
	//商品goodsId小于1则没有此商品，大于则查询商品
	public String detailPage(@PathVariable("goodsId") Long goodsId, HttpServletRequest request) {
		if (goodsId < 1) {
			return "error/error_5xx";
		}

		NewBeeMallGoods goods = newBeeMallGoodsService.getNewBeeMallGoodsById(goodsId);
		
		Map goodsImg = newBeeMallGoodsService.searchGoodsImg(goodsId);

		if (goods == null) {
			NewBeeMallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
		}

		if (Constants.SELL_STATUS_UP != goods.getGoodsSellStatus()) {
			NewBeeMallException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
		}
		//bigOrderBy为key取出所有大图片
		List bigOrderBy = (List) goodsImg.get("bigOrderBy");
		//bigOrderBy为key取出所有小图片
		List smallOrderBy = (List) goodsImg.get("smallOrderBy");
		
		NewBeeMallGoodsDetailVO goodsDetailVO = new NewBeeMallGoodsDetailVO();
		//更改格式返回给前台
		BeanUtil.copyProperties(goods, goodsDetailVO);
		List<NewBeeMallGoodsDetailVO> newBeeMallSearchGoodsBigVOS = BeanUtil.copyList(bigOrderBy,
				NewBeeMallGoodsDetailVO.class);
		List<NewBeeMallGoodsDetailVO> newBeeMallSearchGoodsSmallVOS = BeanUtil.copyList(smallOrderBy,
				NewBeeMallGoodsDetailVO.class);
		//数组用逗号分开，成为数组
		goodsDetailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
		//返回前台
		request.setAttribute("goodsDetail", goodsDetailVO);
		request.setAttribute("goodsBigImgDetail", newBeeMallSearchGoodsBigVOS);
		request.setAttribute("goodsSmallImgDetail", newBeeMallSearchGoodsSmallVOS);
		return "mall/detail";
	}
}
