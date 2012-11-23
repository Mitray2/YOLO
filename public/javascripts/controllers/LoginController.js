$(document).ready(function() {
	
	$('[name="user.name"]').bind('change', capitaliseFirstLetter);
	$('[name="user.lastName"]').bind('change', capitaliseFirstLetter);
	$('[name="user.city"]').bind('change', capitaliseFirstLetter);
	
	$('#secondTest').click(function(e){
		e.preventDefault();
		this.parentElement.parentElement.parentElement.submit();
	});
	$('#firstTest').click(function(e){
		e.preventDefault();
		triggerFields();
		this.parentElement.submit();
	});
	$('#blankForm').click(function(e){
		e.preventDefault();
		triggerFields();
		this.parentElement.parentElement.parentElement.submit();
	});
	
	function triggerFields(){
		$('[name="user.name"]').trigger('keyup');
		$('[name="user.lastName"]').trigger('keyup');
		$('[name="user.city"]').trigger('keyup');
	}
	
	function capitaliseFirstLetter() {
		var value = $(this).val();
		$(this).val(value.charAt(0).toUpperCase() + value.slice(1));
	}
	
	$('input[name^="test.questions"]').click(function() {
		var element = $(this);
		var elementName = element.attr('name');
		var elementNameParts = elementName.split('.');
		
		if(isSecondTest(elementNameParts)) {
			var freeRowCells = [1,2,3];
			var freeColumnCells = [1,2,3]
			var questionNumber = getQuestionNumber(elementNameParts);
			var rowNumber = getRowNumber(elementNameParts);
			var columnNumber = element.attr('column');
			
			delete freeColumnCells[columnNumber - 1];
			delete freeRowCells[rowNumber - 1];
			
			//searching row and unchecking the second element
			var rowNumberForChangingValue; 
			for(var row = 0; row < freeRowCells.length; row++){
				if(typeof freeRowCells[row] != 'undefined') {
					var checkedElement = $('input[name="test.questions[' + questionNumber + '].questions[' + freeRowCells[row] + '].answer.id"][column="' + columnNumber +'"]');
					if(checkedElement.attr('checked')) {
						checkedElement.removeAttr('ckecked');
						rowNumberForChangingValue = freeRowCells[row];
						delete freeRowCells[row];
					}
				}
			}

			var leftRow = findTheOnlyElementValue(freeRowCells);
			
			//finding cheked column in row and removing from freeColumnCells
			var columnNumberForChangingValue;
			for(var column = 0; column < freeColumnCells.length; column++){
				if(typeof freeColumnCells[column] != 'undefined'){
					var checkedElement = $('input[name="test.questions[' + questionNumber + '].questions[' + leftRow + '].answer.id"][column="' + freeColumnCells[column] + '"]');
					if(!checkedElement.attr('checked')){
						columnNumberForChangingValue = freeColumnCells[column];
						break;
					}
				}
			}
			
			//set needed input to checked
			$('input[name="test.questions[' + questionNumber + '].questions[' + rowNumberForChangingValue + '].answer.id"][column="' + columnNumberForChangingValue + '"]').attr('checked', true);
		}
	});

	function isSecondTest(elementNameParts) {
		var result = 0;
		for(var i = 0; i < elementNameParts.length; i++){
			if(startsWith(elementNameParts[i], 'questions')){
				result++;
			}
		}
		return result == 2;
	}
	
	function getQuestionNumber(elementNameParts){
		return getNumber(elementNameParts[1]);
	}
	
	function getRowNumber(elementNameParts){
		return getNumber(elementNameParts[2]);
	}
	
	function getNumber(string){
		return string.replace(/[A-Za-z$-]/g, "").replace(/[\[\]']+/g,'');
	}
	
	function startsWith(str, prefix) {
	    return str.indexOf(prefix) === 0;
	}

	function findTheOnlyElementValue(array){
		for(var index = 0; index < array.length; index++){
			if(typeof array[index] != 'undefined'){
				return array[index];
			}
		}
	}
	
});