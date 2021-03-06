/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallSearchGoodsVO;
import ltd.newbee.mall.dao.NewBeeMallGoodsMapper;
import ltd.newbee.mall.entity.GoodsImg;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.service.NewBeeMallGoodsService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ltd.newbee.mall.dao.GoodsImgMapper;

@Service
public class NewBeeMallGoodsServiceImpl implements NewBeeMallGoodsService {

	@Autowired
	private NewBeeMallGoodsMapper goodsMapper;
	@Autowired
	private GoodsImgMapper goodsImgMapper;

	@Override
	public PageResult getNewBeeMallGoodsPage(PageQueryUtil pageUtil) {
		List<NewBeeMallGoods> goodsList = goodsMapper.findNewBeeMallGoodsList(pageUtil);
		int total = goodsMapper.getTotalNewBeeMallGoods(pageUtil);
		PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
		return pageResult;
	}

	@Override
	public String saveNewBeeMallGoods(NewBeeMallGoods goods) {
		if (goodsMapper.insertSelective(goods) > 0) {
			return ServiceResultEnum.SUCCESS.getResult();
		}
		return ServiceResultEnum.DB_ERROR.getResult();
	}

	@Override
	public void batchSaveNewBeeMallGoods(List<NewBeeMallGoods> newBeeMallGoodsList) {
		if (!CollectionUtils.isEmpty(newBeeMallGoodsList)) {
			goodsMapper.batchInsert(newBeeMallGoodsList);
		}
	}

	@Override
	public String updateNewBeeMallGoods(NewBeeMallGoods goods) {
		NewBeeMallGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
		if (temp == null) {
			return ServiceResultEnum.DATA_NOT_EXIST.getResult();
		}
		goods.setUpdateTime(new Date());
		if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
			return ServiceResultEnum.SUCCESS.getResult();
		}
		return ServiceResultEnum.DB_ERROR.getResult();
	}

	@Override
	public NewBeeMallGoods getNewBeeMallGoodsById(Long id) {
		return goodsMapper.selectByPrimaryKey(id);
	}

	@Override
	public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
		return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
	}

	@Override
	// 商品搜索
	// 返回 PageResul
	public PageResult searchNewBeeMallGoods(PageQueryUtil pageUtil) {
		// 重点
		List<NewBeeMallGoods> goodsList = goodsMapper.findNewBeeMallGoodsListBySearch(pageUtil);
		// 参数封装 对列表查询出的数据进行封装以及转换
		int total = goodsMapper.getTotalNewBeeMallGoodsBySearch(pageUtil);
		List<NewBeeMallSearchGoodsVO> newBeeMallSearchGoodsVOS = new ArrayList<>();
		if (!CollectionUtils.isEmpty(goodsList)) {
			newBeeMallSearchGoodsVOS = BeanUtil.copyList(goodsList, NewBeeMallSearchGoodsVO.class);
			// 返回前端
			for (NewBeeMallSearchGoodsVO newBeeMallSearchGoodsVO : newBeeMallSearchGoodsVOS) {
				String goodsName = newBeeMallSearchGoodsVO.getGoodsName();
				String goodsIntro = newBeeMallSearchGoodsVO.getGoodsIntro();
				// 字符串过长导致文字超出的问题
				if (goodsName.length() > 28) {
					goodsName = goodsName.substring(0, 28) + "...";
					newBeeMallSearchGoodsVO.setGoodsName(goodsName);
				}
				if (goodsIntro.length() > 30) {
					goodsIntro = goodsIntro.substring(0, 30) + "...";
					newBeeMallSearchGoodsVO.setGoodsIntro(goodsIntro);
				}
			}
		}
		// 所有数据封装了一下传给前端 templates/mall/search.html
		PageResult pageResult = new PageResult(newBeeMallSearchGoodsVOS, total, pageUtil.getLimit(),
				pageUtil.getPage());
		return pageResult;
	}

	// 2021/04/05 Service层 手机分类排序
	@Override
	public PageResult searchSecondLevel(PageQueryUtil pageUtil) {
		// 重点
		List<NewBeeMallGoods> goodsList = goodsMapper.searchSecondLevel(pageUtil);
		// 参数封装 对列表查询出的数据进行封装以及转换
		int total = goodsMapper.getTotalNewBeeMallGoodsBySearch(pageUtil);
		List<NewBeeMallSearchGoodsVO> newBeeMallSearchGoodsVOS = new ArrayList<>();
		if (!CollectionUtils.isEmpty(goodsList)) {
			newBeeMallSearchGoodsVOS = BeanUtil.copyList(goodsList, NewBeeMallSearchGoodsVO.class);
			// 返回前端
			for (NewBeeMallSearchGoodsVO newBeeMallSearchGoodsVO : newBeeMallSearchGoodsVOS) {
				String goodsName = newBeeMallSearchGoodsVO.getGoodsName();
				String goodsIntro = newBeeMallSearchGoodsVO.getGoodsIntro();
				// 字符串过长导致文字超出的问题
				if (goodsName.length() > 28) {
					goodsName = goodsName.substring(0, 28) + "...";
					newBeeMallSearchGoodsVO.setGoodsName(goodsName);
				}
				if (goodsIntro.length() > 30) {
					goodsIntro = goodsIntro.substring(0, 30) + "...";
					newBeeMallSearchGoodsVO.setGoodsIntro(goodsIntro);
				}
			}
		}
		// 所有数据封装了一下传给了前端 templates/mall/search.html
		PageResult pageResult = new PageResult(newBeeMallSearchGoodsVOS, total, pageUtil.getLimit(),
				pageUtil.getPage());
		return pageResult;
	}
	   @Override
	    public Map searchGoodsImg(Long goodsId) {
	        List<GoodsImg> goodsImgList = new ArrayList<GoodsImg>();
	        List<GoodsImg> listBig = new ArrayList<GoodsImg>();//一時List
	        List<GoodsImg> listSmall = new ArrayList<GoodsImg>();//一時List
	        List<GoodsImg> bigOrderBy = new ArrayList<GoodsImg>();
	        List<GoodsImg> smallOrderBy = new ArrayList<GoodsImg>();
	        goodsImgList = goodsImgMapper.searchGoodsImg(goodsId);
	        for (int i = 0; i < goodsImgList.size(); i++) {
	            GoodsImg sortedGoodsList = goodsImgList.get(i);
	            if (sortedGoodsList.getDecide() == 0) {
	                listBig.add(sortedGoodsList);
	            } else {
	                listSmall.add(sortedGoodsList);
	            }
	        }
	        bigOrderBy = listBig.stream().sorted(Comparator.comparing(GoodsImg::getOrderby)).collect(Collectors.toList());
	        smallOrderBy = listSmall.stream().sorted(Comparator.comparing(GoodsImg::getOrderby)).collect(Collectors.toList());
	        Map map = new HashMap();
	        map.put("bigOrderBy", bigOrderBy);
	        map.put("smallOrderBy", smallOrderBy);

	        return map;

}
}