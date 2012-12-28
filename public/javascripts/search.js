Search.TYPE_USER = 0;
Search.TYPE_GROUP = 1;
function Search() {
	this.type = null;
	this.uiModel = {
		currentTab : 1,
		currentPage : 1,
		currentSearchPanel : 0,
		searchTab1 : true,
		searchTab2 : false,
		searchTab3 : false,
		totalCount : 0,
		rowId : 0
	};
	this.searchModel = {
			  country : null,
			  city : null,
			  ageMin : null,
		      ageMax : null,
			  sex : null,
			  inCommand : null,
			  bissnessType : null,
			  bissnessSphere : null,
			  marketing : null,
			  sale : null,
			  management : null,
			  finance : null,
			  legal : null,
			  it : null,
			  bmanMin : null,
			  bmanMax : null,
			  idealMin : null,
			  idealMax : null,
			  comutMin : null,
			  comutMax : null,
			  pragmatMin : null,
			  pragmatMax : null,
			  orderBy : null,
			  asc : false,
			  active_column : null
			  
	};
	this.init = function init() {
		_this = this;
		
		//init search panel
		$(".private_item").find("select").each(function(index, item){
			$(item).change(function() {
				_this.uiModel.currentPage = 1;
				search.doSearch();
			});
		});
		$(".private_item > input").each(function(index, item){
			$(item).change(function() {
				_this.uiModel.currentPage = 1;
				search.doSearch();
			});
		});
		
		//init tabs
		$("#searchTab1").click(function(){_this.activateTab(1)});
		$("#searchTab2").click(function(){_this.activateTab(2)});
		$("#searchTab3").click(function(){_this.activateTab(3)});
		_this.activateTab(1);
		
		//init order by
		_initOrderBy();
		
		//init column toolips
		$(".tip").each(function(index, item) {
			_this.tipColumn($(item));
		})
		
		//init seach accordion
		$("#searchTitle1 > a").click(function() { _this.toggleSearchPanel(1); });
		$("#searchTitle2 > a").click(function() { _this.toggleSearchPanel(2); });
		$("#searchTitle3 > a").click(function() { _this.toggleSearchPanel(3); });
		_this.toggleSearchPanel(2);
		_this.toggleSearchPanel(3);
	};
	
	var _initOrderBy = function() {
		$("#column_country").click(function(){_this.doChangeOrder("country")});
		$("#column_city").click(function(){_this.doChangeOrder("city")});
		$("#column_sex").click(function(){_this.doChangeOrder("sex")});
		$("#column_age").click(function(){_this.doChangeOrder("age")});
		$("#column_predpr").click(function(){_this.doChangeOrder("predpr")});
		$("#column_ideal").click(function(){_this.doChangeOrder("ideal")});
		$("#column_communic").click(function(){_this.doChangeOrder("communic")});
		$("#column_pragmatic").click(function(){_this.doChangeOrder("pragmatic")});
		$("#column_businessType").click(function(){_this.doChangeOrder("businessType")});
		$("#column_businessSphere").click(function(){_this.doChangeOrder("businessSphere")});
		$("#column_marketing").click(function(){_this.doChangeOrder("marketing")});
		$("#column_sale").click(function(){_this.doChangeOrder("sale")});
		$("#column_management").click(function(){_this.doChangeOrder("management")});
		$("#column_right").click(function(){_this.doChangeOrder("right")});
		$("#column_finance").click(function(){_this.doChangeOrder("finance")});
		$("#column_lastName").click(function(){_this.doChangeOrder("lastName")});
		$("#column_it").click(function(){_this.doChangeOrder("it")});
		$("#column_more").click(function(){_this.doChangeOrder("more")});
		$("#column_command").click(function(){_this.doChangeOrder("command")});
		$("#column_phase").click(function(){_this.doChangeOrder("phase")});
		$("#column_name").click(function(){_this.doChangeOrder("name")});
		$("#column_count").click(function(){_this.doChangeOrder("count")});
		
	}
	
	this.activateTab = function activateTab(tabIndex) {
		for (var i = 1; i <= 3; i++) {
			if (i == tabIndex) {
				$("#searchTab" + i).addClass("active");
				$(".searchResultColumn" + i).show();
			} else {
				$("#searchTab" + i).removeClass("active");
				$(".searchResultColumn" + i).hide();
			}
		}
		_this.uiModel.currentTab = tabIndex;
	}
	
	this.toggleSearchPanel = function toggleSearchPanel(panelIndex) {
		if (!$("#searchPanel" + panelIndex).is(":visible")) {
			$("#searchPanel" + panelIndex).show();
			$("#searchTitle" + panelIndex + " > span").removeClass("dropdown_close");
			$("#searchTitle" + panelIndex + " > span").addClass("dropdown_open");
			$("#searchTitle" + panelIndex).addClass("open");
			$("#searchTitle" + panelIndex).removeClass("close");
		} else {
			$("#searchPanel" + panelIndex).hide();
			$("#searchTitle" + panelIndex + " > span").removeClass("dropdown_open");
			$("#searchTitle" + panelIndex + " > span").addClass("dropdown_close");
			$("#searchTitle" + panelIndex).removeClass("open");
			$("#searchTitle" + panelIndex).addClass("close");
		}
	}
	
	this.doChangeOrder = function doChangeOrder(columnName) {
		_this = this;
		if (_this.searchModel.orderBy == columnName) {
			//column not changed. just change sort direction.
			_this.searchModel.asc = !_this.searchModel.asc;
			$("#column_" + columnName + " > span").removeClass(_this.searchModel.asc ? "sort_up" : "sort_down");
			$("#column_" + columnName + " > span").addClass(!_this.searchModel.asc ? "sort_up" : "sort_down");
		} else {
			//column changed. set search direction to default
			_this.searchModel.asc = false;
			//change old column state
			$("#column_" + _this.searchModel.orderBy + " > a").removeClass("current");
			$("#column_" + _this.searchModel.orderBy + " span").first().removeClass("ic_" + _this.searchModel.orderBy + "_active");
			$("#column_" + _this.searchModel.orderBy + " span").first().addClass("ic_" + _this.searchModel.orderBy + "_hover");
			$("#column_" + _this.searchModel.orderBy + " > span").removeClass("sort_up");
			$("#column_" + _this.searchModel.orderBy + " > span").removeClass("sort_down");
			$("#column_" + _this.searchModel.orderBy + " > span").addClass("sort");
			//change new column state
			$("#column_" + columnName + " > a").addClass("current");
			$("#column_" + columnName + " span").first().removeClass("ic_" + columnName + "_hover");
			$("#column_" + columnName + " span").first().addClass("ic_" + columnName + "_active");
			$("#column_" + columnName + " > span").addClass("sort_up");
		}
		_this.searchModel.orderBy = columnName;
		//console.log("orderBy: " + _this.searchModel.orderBy + " asc: " + _this.searchModel.asc);
		_this.uiModel.currentPage = 1;
		_this.doSearch();
	}
	
	this.doSearch = function doSearch() {
		_this = this;
		$.each(search.searchModel, function(index, item) {
			if (index == "asc" || index == "orderBy") return;
			search.searchModel[index] = $(".private_item").find("#"+index+"").val();
		});
		//console.log(search.searchModel);
		$("#searchLoader").show();
		$.ajax({
			type: "POST",
			url: (_this.type == Search.TYPE_USER ? "memberSearchAjax?page=" : "groupSearchAjax?page=") + _this.uiModel.currentPage,
			dataType: 'json',
			async: false,
			data: JSON.stringify(search.searchModel),
			success: function(data) {
				//console.log(data);
				var table = $(".search-result-block");
				if (_this.uiModel.currentPage == 1) {
					//new search started.
					table.find("tr:gt(0)").remove();
				}
				if (_this.type == Search.TYPE_USER) {
					$.each(data.users, function(key, user) {
						var row = $("<tr class='search-result-item' style='display: none;'>");
						table.append(row);
						row.append("<td class='width-170'><span class='block-clip'><span class='block'><a href='/" + user.id + "'>" + user.lastName + " " + user.name  + "</a></span></span></td>");
						appendFirstTabColumns(user, row);
						appendSecondTabColumns(user, row);
						appendThirdTabColumns(user, row);
						if (user.commandB) {
							row.append("<td class='cntr'><a href='/groupcontroller/index?id=" + user.commandId + "'><span class='team info' id='info_" + (++search.uiModel.rowId) + "' data-tip='" + user.commandName + "'></span></a></td>");
							search.tipInfo($("#info_" + search.uiModel.rowId));
						} else {
							row.append("<td class='cntr'><span class='single'></span></td>");
						}
						row.fadeIn(500);
					});
				} else {
					$.each(data.groups, function(key, group) {
						var row = $("<tr class='search-result-item' style='display: none;'>");
						table.append(row);
						row.append("<td class='width-170'><span class='block-clip'><span class='block'><a href='/groupcontroller/index?id=" + group.id + "'>" + group.name + "</a></span></span></td>");
						appendFirstTabGroupColumns(group, row);
						appendSecondTabGroupColumns(group, row);
						appendThirdTabGroupColumns(group, row);
						if(group.vacancy){
							row.append("<td class='cntr'>" + group.count + "<span id='warn_" + (++search.uiModel.rowId) + "' class='warning' data-tip='Требуются участиники'></span></td>");
							search.tipWarn($("#warn_" + search.uiModel.rowId));
						}
						else{
							row.append("<td class='cntr'>" + group.count + "</td>");
						}
							
						row.fadeIn(500);
					});
				}

				//check if need to continue scrolling
				if (data.pagesCount > _this.uiModel.currentPage) {
					_this.uiModel.currentPage++;
					setTimeout(_isPageBottom, 1000);
				} else {
					$("#searchLoader").hide();
				}
			}
		});
	}
	
	this.tipInfo = function tipInfo(element) {
		element.hover(
				function(e){
					$(".info_popup > p").text($(this).data("tip"));
					$(".info_popup").fadeIn(200);
					var h = $(".info_popup").height();
					$(".info_popup").offset({left:$(this).offset().left + 28, top:$(this).offset().top - h / 2 - 4});
				}, function(e){
					$(".info_popup").fadeOut(100);
				}
			);
	}
	
	this.tipWarn = function tipWarn(element) {
		element.hover(
				function(e){
					var data = $(this).data("tip");
					if (data && data != "") {
						$(".warning_popup").html(data);
						$(".warning_popup").fadeIn(200);
						var w = $(".warning_popup").width();
						$(".warning_popup").offset({left:$(this).offset().left - w/2, top:$(this).offset().top - 40});
					}
				}, function(e){
					$(".warning_popup").hide();
				}
			);
	}
	
	this.tipColumn = function tipColumn(element) {
		element.hover(
				function(e){
					var data = $(this).data("tip");
					if (data && data != "") {
						$(".icon_popup").html(data);
						$(".icon_popup").fadeIn(200);
						var w = $(".icon_popup").width();
						$(".icon_popup").offset({left:$(this).offset().left - w/2, top:$(this).offset().top - 40});
					}
				}, function(e){
					$(".icon_popup").hide();
				}
			);
	}
	
	var _isPageBottom = function() {
		var result = _isScrolledIntoView($("#searchLoader")) || _isScrolledIntoView($("footer"));
	    if (result) {
	    	_this.doSearch();
	    } else {
	    	setTimeout(_isPageBottom, 1000);
	    }
	};
	
	var _isScrolledIntoView = function(elem) {
	    var docViewTop = $(window).scrollTop();
	    var docViewBottom = docViewTop + $(window).height();
	    var elemTop = elem.offset().top;
	    var elemBottom = elemTop + elem.height();
	    return ((elemBottom >= docViewTop) && (elemTop <= docViewBottom));
	}
	
}
function addIcDiagram(value, info, row, hidden){
	if (value == 1){
		addCell(row, "searchResultColumn3", "<span class='ic_one info' id='info_" + (++search.uiModel.rowId) + "' data-tip='" + info + "'></span>", hidden);
		search.tipInfo($("#info_" + search.uiModel.rowId));
	}
	else{
		if (value == 2){
			addCell(row, "searchResultColumn3", "<span class='ic_two info' id='info_" + (++search.uiModel.rowId) + "' data-tip='" + info + "'></span>", hidden);
			search.tipInfo($("#info_" + search.uiModel.rowId));
		}
		else{
			if (value == 3){
				addCell(row, "searchResultColumn3", "<span class='ic_three info' id='info_" + (++search.uiModel.rowId) + "' data-tip='" + info + "'></span>", hidden);
				search.tipInfo($("#info_" + search.uiModel.rowId));
			}
			else{
				if (value == 4){
					addCell(row, "searchResultColumn3", "<span class='ic_four info' id='info_" + (++search.uiModel.rowId) + "' data-tip='" + info + "'></span>", hidden);
					search.tipInfo($("#info_" + search.uiModel.rowId));
				}
			}
		}
	}
}

function addIcWarning(value, row, hidden){
	if (value == true){
		addCell(row, "searchResultColumn3", "<span id='warn_" + (++search.uiModel.rowId) + "' class='warning' data-tip='Требуются участиники'></span>", hidden);
		search.tipWarn($("#warn_" + search.uiModel.rowId));
	}
	else{
		addCell(row, "searchResultColumn3", "<span class='attention'></span>", hidden);
	}
}


function appendFirstTabGroupColumns(group, row) {
	var hidden = search.uiModel.currentTab != 1;
	addCell(row, "searchResultColumn1", "<a href=''><img src='/public/images/boilerplate/pic4.jpg' alt='' class='search-result-pic'></a>", hidden);
	addCell(row, "searchResultColumn1", group.country, hidden);
	row.append("<td class='searchResultColumn1 width-70' "+getHiddenCss(hidden)+"><span class='block-clip'><span class='block'>"+group.city+"</span></span></td>");
	addCell(row, "searchResultColumn1", group.age, hidden);
	addCell(row, "searchResultColumn1 cntr", group.international, hidden);
	addCell(row, "searchResultColumn1 cntr", group.status, hidden);
	addCell(row, "searchResultColumn1 cntr", "<span class='info' id='info_" + (++search.uiModel.rowId) + "' data-tip='"+group.info+"'></span>", hidden);
	search.tipInfo($("#info_" + search.uiModel.rowId));
	
}
function appendSecondTabGroupColumns(group, row) {
	var hidden = search.uiModel.currentTab != 2;
	addCell(row, "searchResultColumn2", group.businessman, hidden);
	if(group.idealize){
		addCell(row, "searchResultColumn2", group.idealist + "<span id='warn_" + (++search.uiModel.rowId) + "' class='warning' data-tip='Требуются участиники'></span>", hidden);
		search.tipWarn($("#warn_" + search.uiModel.rowId));
	}
	else{
		addCell(row, "searchResultColumn2", group.idealist, hidden);
	}
	if(group.communication){
		addCell(row, "searchResultColumn2", group.communicant + "<span id='warn_" + (++search.uiModel.rowId) + "' class='warning' data-tip='Требуются участиники'></span>", hidden);
		search.tipWarn($("#warn_" + search.uiModel.rowId));
	}
	else{
		addCell(row, "searchResultColumn2", group.communicant, hidden);
	}
	if(group.pragmatizca){
		addCell(row, "searchResultColumn2", group.pragmatist + "<span id='warn_" + (++search.uiModel.rowId) + "' class='warning' data-tip='Требуются участиники'></span>", hidden);
		search.tipWarn($("#warn_" + search.uiModel.rowId));
	}
	else{
		addCell(row, "searchResultColumn2", group.pragmatist, hidden);
	}
	
	row.append("<td class='searchResultColumn2 width-90' "+getHiddenCss(hidden)+"><span class='block-clip'><span class='block'>"+group.businessType+"</span></span></td>");
//	addCell(row, "searchResultColumn2", group.businessType, hidden);
	row.append("<td class='searchResultColumn2 width-90' "+getHiddenCss(hidden)+">"+group.businessSphere+"</td>");
//	addCell(row, "searchResultColumn2", group.businessSphere, hidden);
	addCell(row, "searchResultColumn2", group.phase, hidden);

}

function appendThirdTabGroupColumns(group, row) {
	var hidden = search.uiModel.currentTab != 3;
	addIcWarning(group.marketing, row, hidden);
	addIcWarning(group.sale, row, hidden);
	addIcWarning(group.management, row, hidden);
	addIcWarning(group.legal, row, hidden);
	addIcWarning(group.finance, row, hidden);
	addIcWarning(group.it, row, hidden);
	addIcWarning(group.other, row, hidden);
}


function appendThirdTabColumns(user, row) {
	var hidden = search.uiModel.currentTab != 3;
	
	addIcDiagram(user.marketing, user.marketingInfo, row, hidden);
	addIcDiagram(user.sale, user.saleInfo,  row, hidden);
	addIcDiagram(user.management, user.managementInfo, row, hidden);
	 
	addIcDiagram(user.legal, user.legalInfo, row, hidden);
	addIcDiagram(user.finance, user.financeInfo, row, hidden);
	addIcDiagram(user.it, user.itInfo, row, hidden);
	addIcDiagram(user.other, user.otherInfo, row, hidden);
	

}

function appendSecondTabColumns(user, row) {
	var hidden = search.uiModel.currentTab != 2;
	addCell(row, "searchResultColumn2", user.businessman, hidden);
	addCell(row, "searchResultColumn2", user.idealist, hidden);
	addCell(row, "searchResultColumn2", user.communicant, hidden);
	addCell(row, "searchResultColumn2", user.pragmatist, hidden);
	row.append("<td class='searchResultColumn2 width-90' "+getHiddenCss(hidden)+"><span class='block-clip'><span class='block'>"+user.businessType+"</span></span></td>");
	row.append("<td class='searchResultColumn2 width-90' "+getHiddenCss(hidden)+">"+user.businessSphere+"</td>");
//	addCell(row, "searchResultColumn2", user.businessType, hidden);
//	addCell(row, "searchResultColumn2", user.businessSphere, hidden);
	addCell(row, "searchResultColumn2 cntr", "Yes", hidden);

}

function appendFirstTabColumns(user, row) {
	var hidden = search.uiModel.currentTab != 1;
	if (user.haveAvatar) {
		addCell(row, "searchResultColumn1", "<a href=''><img src='"+fileStoreURL+"/avatars/" + user.id + "/avatar_55.jpg' alt='' class='search-result-pic'></a>", hidden);
	} else {
		if (user.sex) {
			addCell(row, "searchResultColumn1", "<a href=''><img src='/public/images/boilerplate/avam.png' alt='' class='search-result-pic'></a>", hidden);
		} else {
			addCell(row, "searchResultColumn1", "<a href=''><img src='/public/images/boilerplate/avaf.png' alt='' class='search-result-pic'></a>", hidden);
		}
	}
	addCell(row, "searchResultColumn1", user.country, hidden);
	row.append("<td class='searchResultColumn1 width-90' "+getHiddenCss(hidden)+"><span class='block-clip'><span class='block'>"+user.city+"</span></span></td>");
//	addCell(row, "searchResultColumn1", user.city, hidden);
	if (user.sex) {
		addCell(row, "searchResultColumn1", "<img src='/public/images/boilerplate/ico-man.gif' alt=''>M", hidden);
	} else {
		addCell(row, "searchResultColumn1", "<img src='/public/images/boilerplate/ico-woman.gif' alt=''>F", hidden);
	}
	addCell(row, "searchResultColumn1", user.age, hidden);
	row.append("<td class='searchResultColumn1 width-80' "+getHiddenCss(hidden)+">"+user.lastSeen+"</td>");
//	addCell(row, "searchResultColumn1", user.lastSeen, hidden);
	addCell(row, "searchResultColumn1 cntr", "<span class='info' id='info_" + (++search.uiModel.rowId) + "' data-tip='"+user.info+"'></span>", hidden);
	search.tipInfo($("#info_" + search.uiModel.rowId));

}

function addCell(row, cssClass, content, hidden) {
	row.append("<td class='"+cssClass+"' "+getHiddenCss(hidden)+">"+content+"</td>");
}

function getHiddenCss(hidden) {
	if (hidden) {
		return " style='display: none;' ";
	} else {
		return "";
	}
}