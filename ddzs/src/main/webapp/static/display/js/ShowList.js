/*
 * showList对象的作用就是控制在页面列表数据信息上下滚动切换以及翻页；
 * 3个必传参数
 * @tRow    列表的行数
 * @tCol    列表的列数
 * @otherInfo  showList的其余信息，如果没有其他信息，传{}，包括以下属性，可选：
 * 	  otherInfo.showType  列表显示的类型 0:翻页形式   1：滚屏首尾不允许相接  2:固定焦点形式 3：滚屏首尾允许相接 默认：0
 *	  otherInfo.wnd    　　 页面的window对象  默认：iPanel.mainFrame 
 *    otherInfo.isLoop    页面焦点是否循环	 默认:1
 *    otherInfo.pageId     页码信息的id	默认：""
 *    otherInfo.rangeType   focusPos的编排方式 "row":行优先排列  "col":列优先排列, 默认行优先排序
 
 //分批取数据的信息
 *    otherInfo.isAuto       是否是自动分批取数据，默认为0，为1时必须传一下参数，且要加载onDataReady方法，在页面调用getFirstData时需要传入ajaxUrl
 *    otherInfo.dataName     如果是分批取数据，此参数必传，页面数据源的名字
 *    otherInfo.dataWnd      如果是分批取数据，此参数必传，页面数据源的wnd对象
 *    otherInfo.jspDataName  后台jsp页面数据列表的名字
  
 //滑动焦点的信息
 *    otherInfo.focusDiv   页面焦点的ID名称或者定义为滑动对象，例如："focusDiv" 
 *                         或 new showSlip("focusDiv",0,3,40);  默认：""
 *    otherInfo.focusDivInfo  json对象，Div焦点的移动信息，如果传了focusDiv，就必须传此参数，
 *    						  例：{top:20,left:20,area:-1,topStep:110,leftStep:10}, 
 *    otherInfo.focusDivInfo.top
 *                          .left  top,left为焦点初始位置(focusPos为0时)的信息
 *    otherInfo.focusDivInfo.area  Div焦点的区域，取值 1：常规的元素焦点 -1：列表焦点（焦点在下面)
 *    otherInfo.focusDivInfo.topStep  top的步长，可以是数值如120，也可以是数组如[10,20,10,20.....],如果没有传0
 *    otherInfo.focusDivInfo.leftStep left的步长，可以是数值如120，也可以是数组如[10,20,10,20.....],如果没有传0
 */
//iPanel.debug("qqqqq");

function ShowList(tRow, tCol, otherInfo)
{
    /*ShowList的激活状态*/
	this.status = 1;
	/*是否是第一次刷新 0：不是 1：是*/
	this.firstFlag = 1;
	this.ctrlFlag = 0;
	this._tRow = tRow;
	this._tCol = tCol;
	this._isPageFull;
	/*一页列表可展示的数据量；*/
	this._listSize = this._tRow * this._tCol ;  

	this._focusDiv = ("undefined" == typeof(otherInfo.focusDiv)) ? "" : otherInfo.focusDiv;
	this._isPageFull = ("undefined" == typeof(otherInfo.isPageFull)) ? 1 : otherInfo.isPageFull;
	/*如果有focusDiv的信息，则初始化一下属性*/
	if("" != this._focusDiv)   
	{
		//iPanel.debug("in 000 otherInfo.focusDivInfo.area==" + otherInfo.focusDivInfo.area);
		/*记录每行焦点的位置*/
 		this._rowPlace = [otherInfo.focusDivInfo.top];   
		for(var i = 1; i < this._tRow; i++)
		{
		    /*如果是数组*/
			if("object" == typeof(otherInfo.focusDivInfo.topStep))
			{
				this._rowPlace[i] = this._rowPlace[i-1] + otherInfo.focusDivInfo.topStep[i-1] * otherInfo.focusDivInfo.area;
			}
			else
			{
				this._rowPlace[i] = this._rowPlace[0] + otherInfo.focusDivInfo.topStep * i * otherInfo.focusDivInfo.area;
			}
		}
		/*记录每列焦点的位置*/
		this._colPlace = [otherInfo.focusDivInfo.left];  
		for(var i = 1; i < this._tCol; i++)
		{
		    /*如果是数组*/
			if("object" == typeof(otherInfo.focusDivInfo.leftStep))
			{
				this._colPlace[i] = this._colPlace[i-1] + otherInfo.focusDivInfo.leftStep[i-1] * otherInfo.focusDivInfo.area;
			}
			else
			{
				this._colPlace[i] = this._colPlace[0] + otherInfo.focusDivInfo.leftStep * i * otherInfo.focusDivInfo.area;
			}
		}		
	}
	
	this._showType = ("undefined" == typeof(otherInfo.showType)) ? 0 : otherInfo.showType;
	this._wnd = ("undefined" == typeof(otherInfo.wnd)) ? iPanel.mainFrame : otherInfo.wnd;
	this._isLoop = ("undefined" == typeof(otherInfo.isLoop)) ? 1 : otherInfo.isLoop;  
	this._rangeType = ("undefined" == typeof(otherInfo.rangeType)) ? "row" : otherInfo.rangeType;
	if("row" != this._rangeType && "col" != this._rangeType) alert("showlist rangeType is not correct");
	
	//页码信息
	this._pageId = ("undefined" == typeof(otherInfo.pageId)) ? "" : otherInfo.pageId; 

	this._isAuto =  ("undefined" == typeof(otherInfo.isAuto)) ? 0 : otherInfo.isAuto;
	if(1 == this._isAuto)
	{
	    /*检测数据的计数器*/
		this._dataCheckTimer = 0;  
		/*showlist承载的数据名，仅在1 == this._isAuto时存在并有效，用于自动取数据后*/
		this._dataName = otherInfo.dataName; 
		/*数据所在的window对象*/
		this._dataWnd = otherInfo.dataWnd;  
		this._jspDataName = otherInfo.jspDataName;
		this._ajaxUrl = "";
		/*数据获得状态的检测数组,下标是数据的检测点*/
		this._dataPageReadyFlagArr = []; 
		/*数据请求状态的检测数组,下标是数据的检测点*/
		this._dataPageGetFlagArr = [];
		/*数据不足时,延时展示列表的计时器 */
		this._timeout;  
	}
	/*列表的运动方向*/
	this._moveDirection;  
	/*当前页面焦点的位置*/
	this.focusPos = 0; 
	/*前一个页面焦点的位置*/
	this.lastFocusPos = 0;   
	/*当前页面焦点的行索引*/

	this._currRow; 
	 /*当前页面焦点的列索引 */
	this._currCol; 
	/*前一个页面焦点的行索引*/
	this._lastRow;  
	/*前一个页面焦点的列索引*/
	this._lastCol;  
	/*数据焦点的位置*/
	this.position = 0; 
	/*前一个数据焦点的位置*/
	this.lastPosition = 0; 
	/*总数据量*/
	this._dataSize = 0;
	/*当前页 第一页为1*/
	this._currPage;  
	/*总页数*/
	this._totalPage;  
}

/**
 * 初始化展示ShowList的展示参数
 * @param {int} dataSize:总数据量
 * @param {int} initFocusPos：初始页面焦点的位置
 * @param {int} initPosition：初始数据焦点的位置
 * @param {int} isEnable 是否激活列表
 */
ShowList.prototype.initParam = function(dataSize, initFocusPos, initPosition, isEnable)
{
	//iPanel.debug("in initParam 0000000  initFocusPos===" +　initFocusPos);
	/*如果是分批取数据,在getFirstData时会对this.position赋值，页面调用是必须要保证传入getFirstData的position值和传入initParam的position一致*/
	//if(1 == this._isAuto && initPosition != this.position)  alert("init data position not equal getFirstData position"); 
	this.status = undefined == isEnable ? 1 : isEnable;
	this._dataSize = dataSize;
	var initFocusPos = parseInt(initFocusPos);
	var initPosition = parseInt(initPosition);
	if(0 == dataSize) this.position = 0;
	else if(initPosition>=dataSize) this.position = dataSize-1;
	else if(initPosition<0) this.position = 0;
	else this.position = initPosition;
	
	this._currPage = Math.ceil( (this.position + 1) / this._listSize);
	this._totalPage = Math.ceil( this._dataSize / this._listSize);
	
	var temp = (this._dataSize < this._listSize) ? this._dataSize : this._listSize;
	if(initFocusPos>=temp) this.focusPos = temp-1;
	else if(initFocusPos<0) this.focusPos = 0;
	else this.focusPos = initFocusPos;
	//iPanel.debug("in initParam 0000000 this.focusPos===" +this.focusPos);
	/*在下面两个条件下，focusPos完全由position决定*/
	if((2 != this._showType && this._dataSize < this._listSize) || 0 == this._showType) this.focusPos = this.position % this._listSize;
	
	this.lastFocusPos = this.focusPos;
	this.lastPosition = this.position;
	/*如果是分批取数据*/
	if(1 == this._isAuto)  
	{
		var currDataPage = Math.floor(this.position / this._listSize);
		/*再把本页数据取一遍*/
		this._autoGetData(currDataPage);   
		/*取上下页的数据*/
		if(this._dataSize > this._listSize)
		{
			//iPanel.debug("in initParam 00000000 currDataPage + 1 ========"  +  (currDataPage + 1));
			/*先取上页数据*/
			this._autoGetData(currDataPage + 1);  
			//iPanel.debug("in initParam 00000000 (currDataPage + 1 + this._totalPage) % this._totalPage ========"  +  (currDataPage + 1 + this._totalPage) % this._totalPage);
			//iPanel.debug("in initParam 00000000 (currDataPage - 1 + this._totalPage) ========"  +  (currDataPage - 1 + this._totalPage));
			/*如果下页和上页不是一批数据,再取下页数据*/
			if((currDataPage + 1 + this._totalPage) % this._totalPage != (currDataPage - 1 + this._totalPage) % this._totalPage)
			{
				this._autoGetData(currDataPage - 1);
			}
		}
	}
	/*如果有focusDiv的信息*/
	if("" != this._focusDiv)  
	{
		var focusDivName = "string" == typeof(this._focusDiv) ? this._focusDiv : this._focusDiv.divName;
		/*没有数据时隐藏焦点*/
		if(0 == this._dataSize)  
		{
		//	this._wnd.document.getElementById(focusDivName).style.visibility = "hidden";	
		}
		else
		{
		//	this._wnd.document.getElementById(focusDivName).style.visibility = "visible";	
		}
	}	
	//iPanel.debug("in initParam 1111111　this.focusPos===="+ this.focusPos);
}

/**
 * 初始化展示ShowList的展示参数
 * @param {bool} isNeedRefreshList:是否需要重绘列表（页面调用时可以不传此参数）
 */
ShowList.prototype.startShow = function(isNeedRefreshList)
{
	//iPanel.debug("in startShow 0000000");
	//iPanel.debug("in startShow 1111111");
	var refreshListFlag = (undefined == isNeedRefreshList) ? true : isNeedRefreshList;
	
	/*如果是分批取数据,*/
	if(1 == this._isAuto && this._dataSize > this._listSize)
	{
		var currDataPage = Math.floor(this.position / this._listSize);
		var lastDataPage = Math.floor(this.lastPosition / this._listSize);
		//iPanel.debug("in startShow 0000000 this.position ====" + this.position + "  currDataPage ===== " + currDataPage);
		//iPanel.debug("in startShow 0000000 this.lastPosition ====" + this.lastPosition + "  lastDataPage ===== " + lastDataPage);
		/*如果焦点所在的数据页有更新,预取下一页数据*/
		if(currDataPage != lastDataPage)
		{
			this._autoGetData(currDataPage);
			if(this._moveDirection > 0) this._autoGetData(currDataPage + 1);
			else this._autoGetData(currDataPage - 1);
		}
		
		/*如果列表需要刷新,但刷新页的数据还未取全,则延时再展示*/
		if(refreshListFlag)
		{
			var dataReadyFlag;
			/*如果不是翻页形式,且是首次刷新列表*/
			if(0 != this._showType && this._dataSize > this._listSize && (1 == this.firstFlag || 2 == Math.abs(this._moveDirection)))  
			{
				var startPos = (this.position - this.focusPos + this._dataSize) % this._dataSize;
				var endPos = (this.position - this.focusPos + this._listSize - 1 + this._dataSize) % this._dataSize;
				//需要判断当前展示的页首和页尾的数据position所在的数据页数据有没有取全
				var startReadyFlag = this._isDataReady(Math.floor(startPos / this._listSize));
				var endReadyFlag = this._isDataReady(Math.floor(endPos / this._listSize));
				dataReadyFlag = startReadyFlag && endReadyFlag;
				//如果数据没取全，触发取数据
				if(!startReadyFlag) this._autoGetData(Math.floor(startPos / this._listSize));
				if(!endReadyFlag) this._autoGetData(Math.floor(endPos / this._listSize));
			}
			else
			{
				dataReadyFlag = this._isDataReady(currDataPage);
			}
			
			//iPanel.debug("in startShow 1111111 dataReadyFlag =========" +　dataReadyFlag);
			if(!dataReadyFlag)
			{
				var self = this;
				this._wnd.clearTimeout(this._timeout);
				/*如果10次检测数据都没取全的话，说明分批取数据发生异常，避免页面死循环，发送错误消息，showlist停止刷新*/
				if(this._dataCheckTimer > 10)  
				{
					//iPanel.debug("in ShowList DATA_ERROR DATA_ERROR DATA_ERROR========");
					/*清空计数器*/
					this._dataCheckTimer = 0;  
					/*DATA_ERROR*/
					iPanel.sendSimulateEvent(257,9408,0);  
				}	
				else
				{
				    /*计数器加1*/
					this._dataCheckTimer++;  
					this._timeout = this._wnd.setTimeout(function(){self.startShow()},200);
				}
				return;
			}
		}	
		/*清空计数器*/
		this._dataCheckTimer = 0;  
	}
	
	
	/*刷新坐标信息*/
	this._refreshCoordinate();
	
	/*失去焦点时页面的动作*/
	this._loseFocusAction({dataPos:this.lastPosition, idPos:this.lastFocusPos});
	
	/*如果存在_focusDiv，则刷新div焦点信息*/
	if("" != this._focusDiv) 
	{
		this._refreshFocusPlace(); 
	}	
	
	/*如果_pageId存在，刷新页码信息*/
	if("" != this._pageId) this._refreshPageInfo();  
		
	/*如果需要重新刷新列表*/
	if(refreshListFlag)  
	{
		this._refreshList();
		this._refreshListAction(this._currPage - 1);
	}
	
	/*得到焦点时页面的动作*/
	if(0 != this._dataSize) 
	{
		/*如果列表时激活的*/
		if(1 == this.status) this._getFocusAction({dataPos:this.position, idPos:this.focusPos});
		else this._unable();
	}
	
	/*做完刷新后置firstFlag = 0*/
	this.firstFlag = 0;
	this.ctrlFlag = 0;
	//iPanel.debug("in startShow 2222");
}

/**
 * 用户按“上下左右”“上页、下页”键的响应方法
 */
ShowList.prototype.upAction = function(){if(1 == this.status){this._moveDirection = -1; this._changeList({rowStep:-1, colStep:0});}}
ShowList.prototype.downAction = function(){if(1 == this.status){this._moveDirection = 1; this._changeList({rowStep:1, colStep:0});}}
ShowList.prototype.leftAction = function(){if(1 == this.status){this._moveDirection = -1; this._changeList({rowStep:0, colStep:-1});}}
ShowList.prototype.rightAction = function(){if(1 == this.status){this._moveDirection = 1; this._changeList({rowStep:0, colStep:1});}}
ShowList.prototype.pageUp   = function(){if(1 == this.status){this._moveDirection = -2; this._changePage(-1);}}
ShowList.prototype.pageDown = function(){if(1 == this.status){this._moveDirection = 2; this._changePage(1);}}
/**
 * 添加事件函数
 * @param {String} _event:事件名称 "onFocus" "onBlur" "haveData" "haveNoData"
 * @param {Object} func:函数对象
 */
ShowList.prototype.addFunc = function(_event, func)
{
	if("onFocus" == _event)
		this._getFocusAction = func;
	else if("onBlur" == _event)
		this._loseFocusAction = func;
	else if("haveData" == _event)
		this._haveData = func;
	else if("haveNoData" == _event)
		this._haveNoData = func;
	else if("onRefreshList" == _event)
		this._refreshListAction = func;
	else if("onEnable" == _event) 


		this._enable = func;
	else if("onUnable" == _event) 
		this._unable = func;
	else if("onDataReady" == _event) 

		this._dataReadyAction = func;
}

/**
 * 上下左右键的入口方法
 * @param {json} direction: 例{rowStep:0, colStep:-1} 只能传 -1，+1
 */
ShowList.prototype._changeList = function(direction)
{
	//iPanel.debug("in _changeList 0000000　direction.rowStep====" + direction.rowStep);
	//iPanel.debug("in _changeList 0000000　direction.colStep====" + direction.colStep);
	/*以下两种情况不做任何动作，直接返回*/
	/*如果没有数据*/
	if(0 == this._dataSize) return;
	
	var step;
	if("row" == this._rangeType) step = direction.colStep + direction.rowStep * this._tCol;
	else step = direction.rowStep+ direction.colStep * this._tRow;
	
	/*如果数据到头或到尾，并且焦点不循环*/
	if(!this._isLoop) //如果焦点不循环
	{
		/*如果多行多列的列表*/
		if(this._tRow > 1 && this._tCol > 1)
		{
			var startPos = this.position - this.focusPos; //页首的数据位置
			var endPos = startPos + this._listSize - 1;    //页尾的数据位置
			//页首已经到头焦点超出页首，或页尾已经到头焦点超出页尾，不做翻页动作
			if((startPos <= 0 && this.position+step<0 ) || ((endPos >= this._dataSize - 1) && (this.position+step>this._dataSize-1))) return;
		}
		/*如果单行或单列的列表*/
		else
		{
			if(this.position+step<0 || this.position+step>this._dataSize-1) return;
		}
	}
	
	
	
	/*排除以上两种情况，可以判定列表信息一定会发生刷新*/
	var isNeedRefreshList = this._refreshInfoByChangeList(step);  
	this.startShow(isNeedRefreshList);
	//iPanel.debug("in _changeList 111111");
}

/**
 * 翻页键的响应方法
 * @param {int} step:方向值，只能传 -1，+1
 */
ShowList.prototype._changePage = function(step)
{
	//iPanel.debug("in _changePage 0000000");
	/*以下两种情况不做任何动作，直接返回*/
	/*如果数据不足一页*/
	if(this._dataSize <= this._listSize) return;
	
	if(!this._isLoop) //如果焦点不循环
	{
		/*如果翻页形式的列表*/
		if(0 == this._showType)
		{
			//数据到第一页或到最后一页，不做翻页动作
			if(this._currPage+step < 1 || this._currPage+step > this._totalPage) return;
		}
		/*如果非翻页形式的列表*/
		else
		{
			var startPos = this.position - this.focusPos; //页首的数据位置
			var endPos = startPos + this._listSize - 1;    //页尾的数据位置
			//页首已经到头做上翻页，或页尾已经到头做下翻页，不做翻页动作
			if((startPos <= 0 && step < 0) || ((endPos >= this._dataSize - 1) && step > 0))  return;
			
		}
	}
	
	/*否则刷新列表信息*/
	this._refreshInfoByChangePage(step);
	/*翻页必定要刷新整个列表*/
	this.startShow(true);  
	//iPanel.debug("in _changePage 1111111");
}

/**
 * 刷新ShowList对象的基本信息,同时维护返回字段,由this._changeList方法调用
 * @param {int} step
 * @return {bool} isNeedRefreshList,是否需要刷新列表
 */
ShowList.prototype._refreshInfoByChangeList = function(step)
{
	//iPanel.debug("in _refreshInfoByChangeList 000000  step===" +　step);
	/*首先维护数据焦点的信息*/
	/*数据焦点的相对位置*/
	var tempPosition = this.position + step;  
	
	if(this._tRow > 1 && this._tCol > 1)  //如果是多行多列，需要对tempPosition和focusPos进行校正
	{
		//如果数据可能铺不满一页
		if(0 == this._showType  || this._dataSize<this._listSize)  
		{
			/*新的position大于最后一个数据时*/
			if(tempPosition > this._dataSize - 1) 
			{
				var tempTotalCol = ("row" == this._rangeType) ? this._tCol : this._tRow;
				/*求取position所在的虚拟行*/
				var tempLastCol = this.position % tempTotalCol;
				var tempLastRow = (this.position - tempLastCol) / tempTotalCol;
				/*求取最后一个数据所在的虚拟行*/
				var dataSizeCol = (this._dataSize - 1) % tempTotalCol;
				var dataSizeRow = (this._dataSize - 1 - dataSizeCol) / tempTotalCol;
				if(tempLastRow < dataSizeRow)
				{
					tempPosition = this._dataSize - 1;
				}
				else
				{
					tempPosition = 0;
				}  
			}
			else if(tempPosition < 0)
			{
				tempPosition = this._dataSize - 1;
			}
		}
		else if(1 == this._showType && (this.focusPos + step < 0 || this.focusPos + step > this._listSize - 1)) //数据可以永远铺满一页且首尾不相接
		{
			/*没更新列表前页首的数据位置*/
			var lastStartPos = (this.position + this._dataSize - this.focusPos) % this._dataSize; 
			/*没更新列表前页尾的数据位置*/
			var lastEndPos = (lastStartPos + this._listSize - 1) % this._dataSize;
			/*更新后列表前页首的数据位置*/
			var tempStartPos = tempPosition - this.focusPos; 
			/*更新后列表前页尾的数据位置*/
			var tempEndPos = tempStartPos + this._listSize - 1;
			
			if(step > 0 && (tempPosition > this._dataSize - 1 || tempEndPos > this._dataSize - 1))  //正方向移动
			{
		         if(lastEndPos != (this._dataSize - 1))  //如果页尾不是最后一个数据
				 {
					 //保持页面焦点不动，使页尾为最后一个数据
					 if(0 == this._isPageFull)
					 {
						 tempPosition = this._dataSize - 1;
					 }
					 else
					 {
						tempPosition = this._dataSize - 1 - (this._listSize - 1 - this.focusPos); 
					 }
				 }
				 else
				 {
					 tempPosition = 0;
				 }
			}
			else if(step < 0 && (tempPosition < 0 || tempStartPos < 0))
			{
				if(lastStartPos != 0)  //如果页头不是第一个数据
				{
					//保持页面焦点不动，使页头为第一个数据
					tempPosition = this.focusPos;
				}
				else
				{
					tempPosition = this._dataSize - 1;
				}
			}
		}
	}
	
	this.lastPosition = this.position;	
	//iPanel.debug("in _refreshInfoByChangeList 000000  tempPosition===" +　tempPosition);
	this.position = this._getPos(tempPosition, this._dataSize);
	
	/*接着维护页面焦点和currPage信息*/
	this.lastFocusPos = this.focusPos;
	if(0 == this._showType)
	{	
		/*当0 == this._showType时，页面焦点和page的信息完全由this.position决定*/
		this.focusPos = this._getPos(this.position, this._listSize);
		
		var tempPage = Math.ceil((this.position+1)/this._listSize);
		/*如果换页的话，需要重新刷新列表*/
		if(this._currPage != tempPage)  
		{
			 this._currPage = tempPage;
			 return true;
		}
		else
		{
			return false;
		}		
	}
	else if(1 == this._showType || 3 == this._showType)
	{
		/*当1 == this._showType时,没有页的概念，无需维护page信息，页面焦点有三种可能的变化*/
		/*当总数据量大于一页时，页面焦点的移动将要超过页面范围，修正页面焦点位置并刷新整个列表*/
		if(this._dataSize > this._listSize && (this.focusPos + step < 0 || this.focusPos + step > this._listSize - 1))  
		{
			//iPanel.debug("in _refreshInfoByChangeList 000000  step ===========" +　step + " this.position ====" + this.position);
			/*从页尾运动到页首*/
			if(1 == this._showType && 0 == this.position && step > 0)  
			{
				this.focusPos = 0;
			}
			/*从页首运动到页尾*/
			else if(1 == this._showType && (this._dataSize - 1) == this.position && step < 0)  
			{
				this.focusPos = this._listSize - 1;
			}
			else if(1 == this._showType && (this._dataSize - 1) == this.position && step > 0 &&0 == this._isPageFull)  
			{
				var temp = (this._dataSize%this._listSize)%this._tRow;
				if(this._tRow > 1 && this._tCol > 1 && temp!=0)  //如果是多行多列，需要对tempPosition和focusPos进行校正
				{
					this.focusPos = this._listSize - 2;
				}
			}
			return true;
		}
		/*当总数据量小于一页时，页面焦点始终等于数据焦点，无需刷新列表*/
		else if(this._dataSize < this._listSize)
		{
			this.focusPos = this.position;
			return false;
		}
		/*其余情况，页面焦点正常的加1减1,无需刷新列表；*/
		else  
		{
			this.focusPos = this._getPos(this.focusPos + step, this._listSize);
			return false;
		}
	}
	else if(2 == this._showType)
	{
		/*页面焦点固定不动*/
	}
	//iPanel.debug("in _refreshInfoByChangeList 111111");
}

/**
 * 刷新ShowList对象的基本信息,由this._changePage方法调用
 * @param {Object} step
 */
ShowList.prototype._refreshInfoByChangePage = function(step)
{
	//iPanel.debug("in _refreshInfoByChangePage 00000000");
	this.lastFocusPos = this.focusPos;
	this.lastPosition = this.position;
	/*如果是以页的概念展示列表*/
	if(0 == this._showType) 
	{
		this._currPage += step;
		if(this._currPage < 1)
		{
			this._currPage = this._totalPage;
		}
		else if(this._currPage > this._totalPage)
		{
			this._currPage = 1;
		}
		/*翻页后页面焦点默认在页首*/
		this.focusPos = 0;
		this.position = (this._currPage - 1) * this._listSize;
		
	}
	/*如果是以单个元素的概念展示列表*/
	else  
	{		
		/*翻页后页面焦点默认不动,没有页的概念，所以不用维护this._currPage*/
		this.position = (this.position + step*this._listSize + this._dataSize)%this._dataSize;
		/*滚屏不允许首尾相接的形式要对焦点进行校正*/
		if(1 == this._showType)  
		{
		    /*页首的数据位置*/
			var startPos = (this.position+this._dataSize-this.focusPos)%this._dataSize; 
			/*页尾的数据位置*/
			var endPos = (startPos + this._listSize - 1) % this._dataSize;
			/*当数据在一页里首尾相接时,要对position进行校正*/
			if(startPos > endPos)  
			{
			    /*正方向翻页*/
				if(step > 0)  
				{
					 if(0 == this._isPageFull)
					 {
						 var tempYu = this._dataSize % 2;
						 if(0 == tempYu)
						 {
							this.position = this._dataSize - 1 - (this._listSize - 1 - this.focusPos);
						 }
						 if(1 == tempYu)
						 {
							 if(this.focusPos == 3)
							 	this.focusPos = 2;
							this.position = this._dataSize - 1 - (this._listSize - 2 - this.focusPos);												
							
						 }

					 }
					 else
					 {
						this.position = this._dataSize - 1 - (this._listSize - 1 - this.focusPos); 
					 }
					
					
				//	this.position = this._dataSize - 1 - (this._listSize - 1 - this.focusPos);
					//this.focusPos = this._listSize - 1;
				}
				/*负方向翻页*/
				else  
				{
					this.position = this.focusPos;
					//this.focusPos = 0;
				}
			}
		}
		
	}

	//iPanel.debug("in _refreshInfoByChangePage 1111111");
}

/**
 * 改变页面焦点的位置
 */
ShowList.prototype._refreshFocusPlace = function()
{
	//iPanel.debug("in _changeFocusPlace 0000000 this.lastFocusPos===" + this.lastFocusPos);
	//iPanel.debug("in _changeFocusPlace 0000000 this.focusPos===" + this.focusPos);
	if("string" == typeof(this._focusDiv))
	{
	    /*如果是多列*/
		if(this._tCol > 1)   
		{
			this._wnd.document.getElementById(this._focusDiv).style.left = this._colPlace[this._currCol];		
		}
		/*如果是多行*/
		if(this._tRow > 1)   
		{
			this._wnd.document.getElementById(this._focusDiv).style.top = this._rowPlace[this._currRow];					
		}
		
	}
	else
	{
		var rowStep = this._currRow - this._lastRow;
		var colStep = this._currCol - this._lastCol;
		//iPanel.debug("in _changeFocusPlace 333 this._currRow====" + this._currRow);
		//iPanel.debug("in _changeFocusPlace 333 this._rowPlace[this._currRow]====" + this._rowPlace[this._currRow]);
		//iPanel.debug("in _changeFocusPlace 333 this._currCol====" + this._currCol);
		//iPanel.debug("in _changeFocusPlace 333 this._colPlace[this._currCol]====" + this._colPlace[this._currCol]);
		/*两坐标都发生改变，或者第一次刷新，直接做定位*/
		if((0 != rowStep && 0 != colStep) || 1 == this.firstFlag) 
		{
			var coordinate = {top:this._rowPlace[this._currRow], left:this._colPlace[this._currCol]};
			this._focusDiv.initPlace(coordinate);
		}	
		else
		{
			this._focusDiv.moveTo({top:this._rowPlace[this._currRow], left:this._colPlace[this._currCol]});
		}
	}	
	//iPanel.debug("in _changeFocusPlace 11111111");
}

/**
 * 刷新整个列表
 */
ShowList.prototype._refreshList = function()
{
	//iPanel.debug("in _refreshList 000000000");
	//this._dataSize<this._listSize说明数据不足一页
	/*0 == this._showType说明在以页的概念展示列表时，当翻到最后一页，数据可能不足一页*/
	if(0 == this._showType  || this._dataSize<this._listSize || (2 == this._showType && 0 == this._isLoop))
	{
		/*取得页首的数据焦点位置*/
		var startPosition = this.position-this.focusPos;
		for(var i=startPosition; i<startPosition+this._listSize; i++)
		{
			if(i<this._dataSize)
			{
				this._haveData({idPos:i-startPosition, dataPos:i});
			}
			else
			{
				this._haveNoData({idPos:i-startPosition});
			}
		}		
	}
	/*数据始终能铺满一页*/
	else  
	{
		/*取得页首的数据焦点位置*/
		var startPosition = (this.position+this._dataSize-this.focusPos)%this._dataSize;
		for(var i=startPosition; i<startPosition+this._listSize; i++)
		{
			
			if(0 == this._isPageFull)
			{
				if(i >= this._dataSize)
				{
					this._haveNoData({idPos:i-startPosition});
				}else
				{
					this._haveData({idPos:i-startPosition, dataPos:i%this._dataSize});
				}
			}
			else
			{
				this._haveData({idPos:i-startPosition, dataPos:i%this._dataSize});
			}
		}
	}
	//iPanel.debug("in _refreshList 1111111");
}
	
/**
 * 刷新页码信息
 */
ShowList.prototype._refreshPageInfo = function()
{
/*	if(this._dataSize >= this._listSize)
	{*/
	//	this._wnd.document.getElementById(this._pageId).style.visibility = "visible";
		//iPanel.debug("in showPageInfo   00000000");
		if (0 == this._dataSize) 
			this._wnd.document.getElementById(this._pageId).innerText = "";
		/*如果是以页的概念展示列表，页码显示的是 */
		else if(0 == this._showType)  
			this._wnd.document.getElementById(this._pageId).innerText =this._currPage + "  /   " + this._totalPage;
		else
			this._wnd.document.getElementById(this._pageId).innerText =(this.position + 1) + "  /   " + this._dataSize;
		//iPanel.debug("in showPageInfo   11111");
/*	}
	else
	{
		
	//	this._wnd.document.getElementById(this._pageId).style.visibility = "hidden";
	}*/
}

/**
 * 刷新坐标信息
 */
ShowList.prototype._refreshCoordinate = function()
{
	//iPanel.debug("in _refreshCoordinate 0000000 this.focusPos====" + this.focusPos);
	if("row" == this._rangeType)
	{
		this._currCol = this.focusPos % this._tCol;
		this._currRow = (this.focusPos - this._currCol) / this._tCol;
		
		this._lastCol = this.lastFocusPos % this._tCol;
		this._lastRow = (this.lastFocusPos - this._lastCol) / this._tCol;
	}
	else
	{
		this._currRow = this.focusPos % this._tRow;
		this._currCol = (this.focusPos - this._currRow) / this._tRow;
		
		this._lastRow = this.lastFocusPos % this._tRow;
		this._lastCol = (this.lastFocusPos - this._lastRow) / this._tRow;
	}
	//iPanel.debug("in _refreshCoordinate 111111 this._currCol====" + this._currCol);
	//iPanel.debug("in _refreshCoordinate 111111 this._currRow====" + this._currRow);
}

//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$激活去激活的方法 begin$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$//
/**
 * 激活列表
 */
ShowList.prototype.enable = function()
{
	this.status = 1;
	this._enable();
}

ShowList.prototype._enable = function()
{
	if("" != this._focusDiv) 
	{
		var focusDivName = "string" == typeof(this._focusDiv) ? this._focusDiv : this._focusDiv.divName;
	//	this._wnd.document.getElementById(focusDivName).style.visibility = "visible";	
	}
	this._getFocusAction({dataPos:this.position, idPos:this.focusPos});
}

/**
 * 去激活列表
 */
ShowList.prototype.unable = function()
{
	this.status = 0;
	this._unable();
}

ShowList.prototype._unable = function()
{
	if("" != this._focusDiv) 
	{
		var focusDivName = "string" == typeof(this._focusDiv) ? this._focusDiv : this._focusDiv.divName;
	//	this._wnd.document.getElementById(focusDivName).style.visibility = "hidden";	
	}
	this._loseFocusAction({dataPos:this.position, idPos:this.focusPos});
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$激活去激活的方法 end$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$//

//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$分批取数据的方法 begin$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$//
/**
 * 初始数据的获得第一批数据
 * @param {num} position：初始数据焦点的位置
 * @param {num} listSize：一页列表的数据量
 * @param {str} ajaxUrl
 */
ShowList.prototype.getFirstData = function(position,listSize,ajaxUrl)
{
    /*分批取数据*/
	if(1 == this._isAuto)  
	{
		//iPanel.debug("in getFirstData 0000000 ajaxUrl ======" + ajaxUrl);
		this._dataCheckTimer = 0; 
		this.ctrlFlag = 1;
		this._ajaxUrl = (-1 == ajaxUrl.indexOf("?")) ? ajaxUrl + "?" : ajaxUrl + "&";
		this._dataPageReadyFlagArr = [];
		this._dataPageGetFlagArr = [];
		this.position = position;
		/*取消延时展示*/
		this._wnd.clearTimeout(this._timeout);  
		/*取第一批数据*/
		this._autoGetData(Math.floor(position / listSize),1);  
	}
	else
	{
		alert("not Auto get data");
	}
}

/**
 * 检测以dataPageIndex为索引的一页数据是否全部存在
 * @param {num} dataPageIndex
 * return boolean
 */
ShowList.prototype._isDataReady = function(dataPageIndex)
{
	/*如果此页数据已经检测过,并全部ready,直接返回*/
	//iPanel.debug("in _isDataReady dataPageIndex========" + dataPageIndex)
	//iPanel.debug("in _isDataReady this._dataPageReadyFlagArr[dataPageIndex]========" + this._dataPageReadyFlagArr[dataPageIndex])
//	alert(this._dataPageReadyFlagArr[dataPageIndex]+"   "+dataPageIndex);
	if(this._dataPageReadyFlagArr[dataPageIndex]) return true;
	
	//iPanel.debug("in _isDataReady 000000 this._dataName====" + this._dataName);
//	alert(this._dataWnd+"  --- "+this._dataName);
	var list = this._dataWnd[this._dataName];
	//iPanel.debug("in _isDataReady list.length========" + list.length);
	this._dataPageReadyFlagArr[dataPageIndex] = true;
	
	var startPos = this._countDataStartPos(dataPageIndex);
	//iPanel.debug("in _isDataReady 1111111");
	for(var i = startPos, l = startPos + this._listSize; i < l && i < this._dataSize; i++)  
	{
		//iPanel.debug("in _isDataReady 1111111 i====" + i);
		if(undefined == list[i])
		{
		//	alert(this._dataWnd[this._dataName].length+"   "+this._dataName);
			//iPanel.debug("in _isDataReady 1111111 isDataReady 0000000====");
			this._dataPageReadyFlagArr[dataPageIndex] = false;
			break;
		}
	}
	//iPanel.debug("in _isDataReady  111111 dataPageIndex==" + dataPageIndex);
	//iPanel.debug("in _isDataReady  111111 isDataReady==" + this._dataPageReadyFlagArr[dataPageIndex]);
	return this._dataPageReadyFlagArr[dataPageIndex];
}

/**
 * 分批取数据
 * @param {num} tempDataPageIndex 数据页的索引
 * @param {num} isFirstData 是否是第一批数据,此参数仅由getFirstData方法传
 */
ShowList.prototype._autoGetData = function(tempDataPageIndex,isFirstData)
{
	var dataPageIndex = 1 == isFirstData ? tempDataPageIndex : (tempDataPageIndex + this._totalPage) % this._totalPage;
	/*如果此页数据已经存在或以取,则无需再取*/
	if(1 != isFirstData && (this._dataPageGetFlagArr[dataPageIndex] || this._isDataReady(dataPageIndex))) return; 
	else
	{
		/*在闭包里定义变量,以便在回调函数里使用*/
		//var jspDataName = this._jspDataName;
		//var listData = this._dataWnd[this._dataName];
		var startPos = this._countDataStartPos(dataPageIndex,isFirstData);
		var dataPageReadyFlagArr = this._dataPageReadyFlagArr;
		var self = this;
		
		//iPanel.debug("in  _autoGetData 111111111111 startPos=========== " +　startPos);
		/*取一页的数据*/
		var ajaxUrl = this._ajaxUrl + "STATION=" + startPos + "&LENGTH=" + this._listSize+"&isFirstData="+isFirstData; 
		//修复返回，数据取不全。只适用于沙特版本四个vod展示页面。
		if(1 == isFirstData&&0 == this._isPageFull)
		{
			ajaxUrl = this._ajaxUrl + "STATION=" + startPos + "&LENGTH=" + 6+"&isFirstData="+isFirstData; 
			
		}
		//iPanel.debug("in  _autoGetData 111111111111 this._ajaxUrl=========== " +　this._ajaxUrl);
		//iPanel.debug("in  _autoGetData 111111111111 self._ajaxUrl=========== " +　self._ajaxUrl);
		var ajaxObj = new AJAX_OBJ(ajaxUrl, autoDataResp);
		/*if(isFirstData) ajaxObj.requestData(1);
		else ajaxObj.requestData();*/
		ajaxObj.sendRequese();
		
		function autoDataResp(data)
		{
			data = eval("("+data+")");
		    /*参数校验*/
			if(undefined == data.countTotal)  alert("jsp countTotal is not available");  
			/*后台返回的数据*/
			var list = data[self._jspDataName];  
			/*如果是第一批数据*/
			if(1 == isFirstData)  
			{
				iPanel.debug("channelistTimeTest=========== dataBack"+ajaxUrl);
				self._dataWnd[self._dataName] = [];
				self._dataSize = self._dataWnd[self._dataName].length = data.countTotal;
				//iPanel.debug("in _autoGetData autoDataResp 00000000 self._dataWnd[self._dataName].length=====" + self._dataWnd[self._dataName].length);
			}
			else
			{
			    /*如果数据不是第一次取数据，且数据发生了改变,重新刷一次数据*/
				if(0 != self._dataSize && self._dataSize != data.countTotal)  
				{
					//iPanel.debug("in _autoGetData autoDataResp 00000000 DATA_CHANGE self._dataSize=====" + self._dataSize);
					//iPanel.debug("in _autoGetData autoDataResp 00000000 DATA_CHANGE data.countTotal=====" + data.countTotal);
					self._dataSize = data.countTotal;
					/*DATA_CHANGE*/
					iPanel.sendSimulateEvent(257,9409,0);  
					//iPanel.debug("in _autoGetData autoDataResp 00000000 DATA_CHANGE self._ajaxUrl=====" + self._ajaxUrl);
					self.getFirstData(self.position,self._listSize,self._ajaxUrl);
					return;
				}
			}
			
			//iPanel.debug("in _autoGetData autoDataResp 00000000 aaaaaa=====");
			var listData = self._dataWnd[self._dataName];
			//iPanel.debug("in _autoGetData autoDataResp 00000000 bbbbbbb=====");
			//iPanel.debug("in _autoGetData autoDataResp 00000000 list.list=====" + list.length);
			/*写入数据*/
			for(var i = startPos, l = startPos + list.length; i < l; i++) 
			{
				//iPanel.debug("in _autoGetData autoDataResp 00000000 iiiiiiiiii=====" + i);
				listData[i] = list[i - startPos];
				//iPanel.debug("in _autoGetData autoDataResp i =====" + i + " listData[i]=======" +　listData[i]);
			}
			dataPageReadyFlagArr[dataPageIndex] = true;
			/*如果是第一批数据回来了*/
			if(1 == isFirstData)  
			{
				iPanel.sendSimulateEvent(257,9410,0);  //DATA_READY
				self._dataReadyAction(data);
			}
		}
	}
	this._dataPageGetFlagArr[dataPageIndex] = true;
}

/**
 * 第一批数据到手，页面由页面触发展示，页面会收到jsp后台页面返回的数据
 */
ShowList.prototype._dataReadyAction = function(){}

//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$分批取数据的方法 end$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$//

/**
 * 根据数据页的索引,计算当前数据页的起始位置
 * {param}dataPageIndex:数据页的索引
 * {param}isFirstData:是否是第一次取数据
 */
ShowList.prototype._countDataStartPos = function(dataPageIndex,isFirstData)
{
	var startPos = dataPageIndex * this._listSize;
	/*如果不是第一次取数据，且不是翻页形式的列表,在数据页的最后一页得多取一些数据*/
	if(1 != isFirstData && this._totalPage - 1 == dataPageIndex && 0 != this._showType) startPos = this._dataSize - this._listSize;
	return startPos;
}

ShowList.prototype._getPos = function(num,size)
{
	return (0 == size) ? 0 : (num + size) % size;
}

//以下四个方法都会收到参数为{idPos:Num, dataPos:Num}的对象，该对象属性idPos为页面焦点的值，属性dataPos为数据焦点的值；
/**
 * 得到焦点的事件监听函数，由页面实现，通过addFunc方法赋值
 */
ShowList.prototype._getFocusAction = function(){}

/**
 * 失去焦点的事件监听函数，由页面实现，通过addFunc方法赋值
 */
ShowList.prototype._loseFocusAction = function(){}

/**
 * 有数据的情况下，刷新单个列表数据的方法，由页面实现，通过addFunc方法赋值
 */
ShowList.prototype._haveData = function(){}

/**
 * 没数据的情况下，刷新单个列表数据的方法，由页面实现，通过addFunc方法赋值
 */
ShowList.prototype._haveNoData = function(){}


/**
 * 刷新整个列表,传出参数pageIndex
 */
ShowList.prototype._refreshListAction = function(){}

//iPanel.debug("wwww");


