$.fn.exists = function () {
	return this.length !== 0;
}

var CommonsManager = function() {
	var self = this;
	
	if (CommonsManager.prototype._singletonInstance) {
		return CommonsManager.prototype._singletonInstance;
	}

	CommonsManager.prototype._singletonInstance = this;

	self.eb = new vertx.EventBus(window.location.protocol + '//'
			+ window.location.hostname + ':' + window.location.port
			+ '/eventbus');
	
	CommonsManager.prototype.eventBus = function() {
		return self.eb;
	}
	
	CommonsManager.prototype.getURLParameters = function () {
		var parameters = decodeURIComponent(window.location.search.slice(1))
	    .split('&')
	    .reduce(function _reduce (/*Object*/ a, /*String*/ b) {
	      b = b.split('=');
	      a[b[0]] = b[1];
	      return a;		
	    }, {});
		
		return parameters;
	}
};

CommonsManager.getInstance = function () {
	var commonsManager = new CommonsManager();
	return commonsManager;
};

var chronos = {
	eb: CommonsManager.getInstance().eventBus,
	urlParameters: CommonsManager.getInstance().getURLParameters
};

(function() {
	// Chronos App Notification API
	
	var self = this;
	var initCallbacks = [];
	
	self.notify = function (action, document, callback) {
		chronos.eb().send('chronos.app.notify', {
			'action': action, 
			'document': document
		}, callback);
	};
	
	self.addInitializer = function (callback) {
		initCallbacks.push(callback);
	}
	
	self.init = function () {
		for (var i=0; i<initCallbacks.length; i++) {
			initCallbacks[i]();
		}
	}
	
}).apply(chronos);

(function() {
	// User Management API
	var self = this;
	
	self.getUsername = function () {
		return chronos.urlParameters()['username'];
	}
	
	self.getSessionID = function () {
		return chronos.urlParameters()['sessionID'];
	}
	
	self.login = function (username, password, callback) {
		if (username.trim() != '' && password.trim() != '') {
			chronos.eb().send('vertx.basicauthmanager.login', 
			{ 'username' : username, 'password' : password }, callback);
		}
	};
	
	self.logout = function(callback) {
		chronos.eb().send('vertx.basicauthmanager.logout', {
			sessionID : self.getSessionID()
		}, callback);
	}
	
	self.authoriseSession = function (sessionId, callback) {
		chronos.eb().send('vertx.basicauthmanager.authorise', {'sessionID': sessionId}, callback);
	};
	
	self.changePassword = function (document, callback) {
		chronos.notify('changePassword', document, callback);
	};
	
}).apply(chronos);

(function() {
	// Mongo Management API
	
	var self = this;
	
	self.find = function (collection, matcher, callback) {
		chronos.eb().send('vertx.mongopersistor', {
			'action': 'find', 
			'collection': collection, 
			'matcher': matcher
		}, callback);
	}
	
	self.save = function (collection, criteria, document, callback) {
		chronos.eb().send('vertx.mongopersistor', {
			'action': 'save', 
			'collection': collection, 
			'criteria': criteria,
			'document': document	
		}, callback);
	}
	
}).apply(chronos);

$(document).ready(function () {
	
	if (chronos.eb().isOpen) {
		chronos.init();
	} else {
		chronos.eb().onopen = function() {
			chronos.init();
		};
		
		chronos.eb().onclose = function() {
			eb = null;
		};
	}

});
