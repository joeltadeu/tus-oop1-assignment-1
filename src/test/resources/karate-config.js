function fn() {
    var config = {};
    // point this to your running app
    config.baseUrl = 'http://localhost:' + karate.properties['port'];
    return config;
}