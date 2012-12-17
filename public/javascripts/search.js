var search = new Search();
$(document).ready(function(){
	search.init();
	search.doSearch();
});
function Search() {
	this.uiModel = {
		currentTab : 1,
		currentPage : 1,
		searchPanel1 : true,
		searchPanel2 : false,
		searchPanel3 : false,
		searchTab1 : true,
		searchTab2 : false,
		searchTab3 : false,
		totalCount : 0
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
			  pragmatMax : null
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
		
		//init seach accordion
		//TODO:
//		if (search.uiModel.searchPanel1) {
//			$("#searchPanel1Content").show();
//			$("#searchPanel1").removeClass("dropdown_closed");
//			$("#searchPanel1").addClass("dropdown_open");
//		} else {
//			$("#searchPanel1Content").hide();
//		}
//		if (search.uiModel.searchPanel2) {
//			$("#searchPanel2Content").show();
//		} else {
//			$("#searchPanel2Content").hide();
//		}
//		if (search.uiModel.searchPanel3) {
//			$("#searchPanel3Content").show();
//		} else {
//			$("#searchPanel3Content").hide();
//		}
	};
	
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
	}
	
	this.doSearch = function doSearch() {
		_this = this;
		$.each(search.searchModel, function(index, item) {
			search.searchModel[index] = $(".private_item").find("#"+index+"").val();
		});
		console.log(search.searchModel);
		console.log("do load page " + _this.uiModel.currentPage);
		$("#searchLoader").show();
		$.ajax({
			type: "POST",
			url: "memberSearchAjax?page=" + _this.uiModel.currentPage,
			dataType: 'json',
			async: false,
			data: JSON.stringify(search.searchModel),
			success: function(data) {
				console.log(data);
				var table = $(".search-result-block");
				if (_this.uiModel.currentPage == 1) {
					//new search started.
					table.find("tr:gt(0)").remove();
				}
				$.each(data.users, function(key, user) {
					var row = $("<tr class='search-result-item' style='display: none;'>");
					table.append(row);
					row.append("<td><a href=''>" + user.name + " " + user.lastName + "</a></td>");
					appendFirstTabColumns(user, row);
					appendSecondTabColumns(user, row);
					appendThirdTabColumns(user, row);
					row.fadeIn(500);
				});
				console.log("data loaded");
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
	
	var _isPageBottom = function() {
		var result = _isScrolledIntoView($("#searchLoader")) || _isScrolledIntoView($("footer"));
		console.log("_isPageBottom: " + result);
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

function appendThirdTabColumns(user, row) {
	var hidden = search.uiModel.currentTab != 3;
	addCell(row, "searchResultColumn3", "<span class='ic_one'></span>", hidden);
	addCell(row, "searchResultColumn3", "<span class='ic_one'></span>", hidden);
	addCell(row, "searchResultColumn3", "<span class='ic_one'></span>", hidden);
	addCell(row, "searchResultColumn3", "<span class='ic_one'></span>", hidden);
	addCell(row, "searchResultColumn3", "<span class='ic_one'></span>", hidden);
	addCell(row, "searchResultColumn3", "<span class='ic_one'></span>", hidden);
	addCell(row, "searchResultColumn3", "<span class='ic_one'></span>", hidden);
	if (user.commandB) {
		addCell(row, "searchResultColumn3 cntr", "<span class='team'></span>", hidden);
	} else {
		addCell(row, "searchResultColumn3 cntr", "<span class='single'></span>", hidden);
	}
}

function appendSecondTabColumns(user, row) {
	var hidden = search.uiModel.currentTab != 2;
	addCell(row, "searchResultColumn2", "45", hidden);
	addCell(row, "searchResultColumn2", "45", hidden);
	addCell(row, "searchResultColumn2", "45", hidden);
	addCell(row, "searchResultColumn2", "45", hidden);
	addCell(row, "searchResultColumn2", user.bissnessType, hidden);
	addCell(row, "searchResultColumn2", user.bissnessSphere, hidden);
	addCell(row, "searchResultColumn2 cntr", "Yes", hidden);
	if (user.commandB) {
		addCell(row, "searchResultColumn2 cntr", "<span class='team'></span>", hidden);
	} else {
		addCell(row, "searchResultColumn2 cntr", "<span class='single'></span>", hidden);
	}
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
	addCell(row, "searchResultColumn1", user.city, hidden);
	if (user.sex) {
		addCell(row, "searchResultColumn1", "<img src='/public/images/boilerplate/ico-man.gif' alt=''>M", hidden);
	} else {
		addCell(row, "searchResultColumn1", "<img src='/public/images/boilerplate/ico-woman.gif' alt=''>F", hidden);
	}
	addCell(row, "searchResultColumn1", user.age, hidden);
	addCell(row, "searchResultColumn1", user.status, hidden);
	addCell(row, "searchResultColumn1", "<span class='info'></span>", hidden);
	if (user.commandB) {
		addCell(row, "searchResultColumn1 cntr", "<span class='team'></span>", hidden);
	} else {
		addCell(row, "searchResultColumn1 cntr", "<span class='single'></span>", hidden);
	}
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