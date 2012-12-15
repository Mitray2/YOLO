var search = new Search();
$(document).ready(function(){
	search.init();
	search.doSearch();
});
function Search() {
	this.uiModel = {
		currentTab : 1,
		currentPage : 1
	};
	this.searchModel = {
			country : null,
			city : null
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
	};
	
	this.doSearch = function doSearch() {
		$.each(search.searchModel, function(index, item) {
			search.searchModel[index] = $(".private_item").find("#"+index+"").val();
		});
		console.log(search.searchModel);
		$.getJSON('ajax/test.json', function(data) {
			var table = $(".search-result-block");
			table.find("tr:gt(0)").remove();
			$.each(data, function(key, val) {
				var row = $("tr");
				table.append(row);
				row.append("<td>xxx</td>");
			});
		});
	}
}