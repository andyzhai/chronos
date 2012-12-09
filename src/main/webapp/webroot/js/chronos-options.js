(function () {

	function Options(options) {
		var self = this;
		
		if (options) {
			self._id = options._id;
			self.useCounter = ko.observable(options.useCounter);
		} else {
			self.useCounter = ko.observable(false);
		}
		
		self.updateOptions = function () {
			var data = ko.toJSON(self);
			var obj = $.parseJSON(data);
			
			chronos.save('timeline.options', {_id: self._id}, obj, function(reply) {
				if (self._id === undefined) {
					self._id = reply._id;
				}
			});	
			return true;
		}
	}
	
	function PasswordChange(details) {
		var self = this;
		
		if (details) {
			self.oldPassword = ko.observable(details.oldPassword);
			self.newPassword = ko.observable(details.newPassword);
			self.confirmPassword = ko.observable(details.confirmPassword);
		} else {
			self.oldPassword = ko.observable();
			self.newPassword = ko.observable();
			self.confirmPassword = ko.observable();
		}
		
		self.changePassword = function () {
			var data = ko.toJSON(self);
			var obj = $.parseJSON(data);
			obj.username = chronos.getUsername();
			obj.sessionID = chronos.getSessionID();
			obj.oldPassword = self.oldPassword();
			obj.newPassword = self.newPassword();
			obj.confirmPassword = self.confirmPassword();
			
			chronos.changePassword(obj, function(reply) {
				console.log(reply);
				self.oldPassword('');
				self.newPassword('');
				self.confirmPassword('');
				$('#passwordUpdateMessage').text(reply.message);
				$('#passwordChangeModal').modal('show');
			});
		}
	}
	
	var optionsViewModel = function () {
		
		var self = this;
		self.passwordChange = new PasswordChange(null);
		self.options = null;
		
		self.init = function () {
			ko.applyBindings(self.passwordChange, document.getElementById('passwordChange'));
			
			chronos.find('timeline.options', {}, function(reply) {
				console.log(reply.status);
				console.log(reply.results);
				if (reply.results.length === 1) {
					self.options = new Options(reply.results[0]);
				} else {
					self.options = new Options(null);
				}
				ko.applyBindings(self.options, document.getElementById('options'));
			});
		};
		
		return {
			init: self.init
		};	
	}
	
	chronos.addInitializer(optionsViewModel().init);

}).apply(chronos);
