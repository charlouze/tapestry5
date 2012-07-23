Tapestry.dojo = {
    findTabContainerManagerForTabContainer : function(tabContainerId) {
        tabContainerManager = $(tabContainerId).getStorage().tabContainerManager;
        if (Object.isUndefined(tabContainerManager)) {
            tabContainerManager = new Tapestry.dojo.TabContainerManager(tabContainerId);
            $(tabContainerId).getStorage().tabContainerManager = tabContainerManager;
        }
        return tabContainerManager;
    }
};

Tapestry.dojo.TabContainerManager = Class.create({
    initialize : function(tabContainerId, options) {
        this.tabContainerId = tabContainerId;
        this.tabContainer = dijit.byId(tabContainerId);
        if (this.tabContainer == null || this.tabContainer.declaredClass != 'dijit.layout.TabContainer') {
            throw tabContainerId + ' is not a dijit.layout.TabContainer.'
        }
        if (!Object.isUndefined(options) && Object.isFunction(options.onChangeTab)) {
            this.tabContainer.watch("selectedChildWidget", options.onChangeTab);
        }
    },

    loadTabFromURL : function(url, id, tabTitle, ajaxParams) {
        if (Object.isUndefined(ajaxParams)) {
            ajaxParams = {}
        }

        Tapestry.ajaxRequest(url, {
            parameters : ajaxParams,
            onSuccess : function(transport) {
                this.processReply(transport.responseJSON, id, tabTitle);
            }.bind(this)
        });
    },

    processReply : function(reply, id, tabTitle) {
        Tapestry.loadScriptsInReply(reply, function() {
            children = this.tabContainer.getChildren();
            var pane;
            for (i = 0; i < children.length; i++) {
                if (children[i].get("title") == tabTitle) {
                    pane = children[i];
                }
            }

            if (Object.isUndefined(pane)) {
                pane = new dijit.layout.ContentPane({
                    enyxId: id,
                    title : tabTitle,
                    content : reply.content,
                    closable : true,
                    parseOnLoad : false
                });
                this.tabContainer.addChild(pane);
                this.tabContainer.selectChild(pane);
            } else {
                pane.set('content', reply.content);
                this.tabContainer.selectChild(pane);
            }
        }.bind(this));
    }
});

Tapestry.dojo.Initializer = {};

Tapestry.Initializer.tabContainer = function(spec) {
    dojo.ready(function() {
        storage = $(this.elementId).getStorage();
        tabContainerManager = new Tapestry.dojo.TabContainerManager(this.elementId, this);
        storage.tabContainerManager = tabContainerManager;
    }.bind(spec));
}