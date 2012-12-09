(function () {
	
	var logoutViewModel = function() {
		
		var self = this;
		self.username = ko.observable('');
		self.sessionID = ko.observable('');
	
		self.eb = CommonsManager.getInstance().eventBus();
	
		self.logout = function() {
			chronos.logout(function(reply) {
				self.sessionID(null);
				window.location.href = window.location.origin + "/";
			});
		}
	
		self.init = function() {
			ko.applyBindings(self, document.getElementById('sessionControl'));
	
			var parameters = CommonsManager.getInstance().getURLParameters()
			self.username(parameters['username']);
			self.sessionID(parameters['sessionID']);
		}
		
		return {
			init: self.init
		}
	};
	
	chronos.addInitializer(logoutViewModel().init);
	
}).apply(chronos);
