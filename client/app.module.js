"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var platform_browser_1 = require("@angular/platform-browser");
var core_1 = require("@angular/core");
var forms_1 = require("@angular/forms");
var http_1 = require("@angular/http");
var app_component_1 = require("./app.component");
var scheduler_module_1 = require("./component/scheduler/scheduler.module");
var search_component_1 = require("./component/search/search.component");
var sentiment_display_component_1 = require("./component/sentiment-display/sentiment-display.component");
var performance_display_component_1 = require("./component/performance-display/performance-display.component");
var sentiment_display_service_1 = require("./component/sentiment-display/sentiment-display.service");
var performance_display_service_1 = require("./component/performance-display/performance-display.service");
var ng2_bootstrap_1 = require("ng2-bootstrap");
var primeng_1 = require("primeng/primeng");
var sentiment_service_1 = require("./service/sentiment.service");
var twitterinho_chart_component_1 = require("./component/twitterinho-chart/twitterinho-chart.component");
var AppModule = (function () {
    function AppModule() {
    }
    return AppModule;
}());
AppModule = __decorate([
    core_1.NgModule({
        declarations: [
            app_component_1.AppComponent,
            search_component_1.SearchComponent,
            sentiment_display_component_1.SentimentDisplayComponent,
            performance_display_component_1.PerformanceDisplayComponent,
            twitterinho_chart_component_1.TwitterinhoChartComponent
        ],
        imports: [
            platform_browser_1.BrowserModule,
            forms_1.FormsModule,
            http_1.HttpModule,
            scheduler_module_1.SchedulerModule,
            ng2_bootstrap_1.ModalModule,
            primeng_1.ChartModule,
            primeng_1.GrowlModule
        ],
        providers: [sentiment_service_1.SentimentService, sentiment_display_service_1.SentimentDisplayService, performance_display_service_1.PerformanceDisplayService],
        bootstrap: [app_component_1.AppComponent]
    })
], AppModule);
exports.AppModule = AppModule;
