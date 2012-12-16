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
		searchPanel3 : false
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
		$(".private_item").find("select").each(function(index, item){
			$(item).change(function() {
				search.doSearch();
			});
		});
		$(".private_item > input").each(function(index, item){
			$(item).change(function() {
				search.doSearch();
			});
		});
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
	
	this.doSearch = function doSearch() {
		$.each(search.searchModel, function(index, item) {
			search.searchModel[index] = $(".private_item").find("#"+index+"").val();
		});
		console.log(search.searchModel);
		$.ajax({
			type: "POST",
			url: "memberSearchAjax",
			dataType: 'json',
			async: false,
			data: JSON.stringify(search.searchModel),
			success: function(data) {
				var table = $(".search-result-block");
				table.find("tr:gt(0)").remove();
				$.each(data, function(key, user) {
					var row = $("<tr class='search-result-item'>");
					table.append(row);
					row.append("<td><a href=''>" + user.name + " " + user.lastName + "</a></td>");
					if (user.haveAvatar) {
						row.append("<td><a href=''><img src='"+fileStoreURL+"/avatars/" + user.id + "/avatar_55.jpg' alt='' class='search-result-pic'></a></td>");						
					} else {
						if (user.sex) {
							row.append("<td><a href=''><img src='/public/images/boilerplate/avam.png' alt='' class='search-result-pic'></a></td>");
						} else {
							row.append("<td><a href=''><img src='/public/images/boilerplate/avaf.png' alt='' class='search-result-pic'></a></td>");
						}
					}
					row.append("<td>"+user.country+"</td>");
					row.append("<td>"+user.city+"</td>");
					if (user.sex) {
						row.append("<td><img src='/public/images/boilerplate/ico-man.gif' alt=''>M</td>");
					} else {
						row.append("<td><img src='/public/images/boilerplate/ico-woman.gif' alt=''>F</td>");
					}
					row.append("<td>"+user.age+"</td>");
					row.append("<td>"+user.status+"</td>");
					row.append("<td><span class='info'></span></td>");
					if (user.commandB) {
						row.append("<td><span class='team'></span></td>");
					} else {
						row.append("<td><span class='single'></span></td>");	
					}
				});
			}
		});
	}
}