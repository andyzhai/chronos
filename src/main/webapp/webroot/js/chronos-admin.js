(function() {
	
	function Timeline(timeline) {
		if (timeline) {
			this._id = timeline._id;
			this.headline = ko.observable(timeline.headline);
			this.type = timeline.type;
			this.text = ko.observable(timeline.text);
			this.startDate = ko.observable(timeline.startDate);
			this.asset = {
				media: ko.observable(timeline.asset.media),
				credit: ko.observable(timeline.asset.credit),
				caption: ko.observable(timeline.asset.caption)
			}
			this.date = ko.observableArray(timeline.date);
		} else {
			this.headline = ko.observable();
			this.type = 'default';
			this.text = ko.observable();
			this.startDate = ko.observable();
			this.asset = {
				media: ko.observable(),
				credit: ko.observable(),
				caption: ko.observable()
			}
			this.date = ko.observableArray([]);
			// requires at least one date
			var d = new Date();
			this.date.push({
				startDate: '' + d.getFullYear() + ',' + (d.getMonth() + 1),
				headline:'',
				text:'',
				amount:0,
				asset: {
					media: '',
					credit: '',
					caption: ''
				}
			});
		}
	}
	
	var chronosAdminViewModel = function () {
		
		var self = this;
		var entryToRemove = null;
		
		self.timeline = new Timeline(null);
	
		self.eb = CommonsManager.getInstance().eventBus();
	 	
		$('#timeline-startDate').datepicker({
			format: 'yyyy,m'
		});
		$('#timeline-endDate').datepicker({
			format: 'yyyy,m'
		});
	
		function hookDates() {
			$('#date-startDate').datepicker({
			    format: 'yyyy,m'
			});
		}
		
		self.init = function () {
			ko.applyBindings(self, document.getElementById('control-panel'));				
			
			chronos.find('timeline', {}, function(reply) {
				console.log(reply.status);
				console.log(reply.results);
				if (reply.results.length === 1) {
					self.timeline = new Timeline(reply.results[0]);
				}
				ko.applyBindings(self.timeline, document.getElementById('timeline'));				
				hookDates();
			});
		};
		
		self.save = function() {
			var data = ko.toJSON(self.timeline);
			var obj = $.parseJSON(data);
			console.log(data);
			chronos.save('timeline', {_id: self.timeline._id}, obj, function(reply) {
				if (self.timeline._id === undefined) {
					self.timeline._id = reply._id;
				}
			});
	    };
		
		self.addDate = function() {
			var i = self.timeline.date().length;
			
			var d = new Date();
			self.timeline.date.push({
				startDate: '' + d.getFullYear() + ',' + (d.getMonth() + 1),
				headline: '',
				text: '',
				amount: 0,
				asset: {
					media: '',
					credit: '',
					caption: ''
				}
			});
			
			$('#date-entry-' + i)[0].scrollIntoView();
	    };
	    
	    self.markForRemoval = function(entry) {
	    	self.entryToRemove = entry;
	    };
	    
	    self.cancelRemoval = function() {
	    	self.entryToRemove = null;
	    };
	    
	    self.confirmRemoval = function() {
	    	if (self.entryToRemove !== null) {
	    		self.timeline.date.remove(self.entryToRemove);
	    	}
	    	$('#confirmRemoval').modal('hide')
	    };
	    
		return {
			eb: self.eb,
			init: self.init
		}
	}
	
	chronos.addInitializer(chronosAdminViewModel().init);

}).apply(chronos);
