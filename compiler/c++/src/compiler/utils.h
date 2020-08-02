//
// Created by timo on 7/29/20.
//

#ifndef ARKOICOMPILER_UTILS_H
#define ARKOICOMPILER_UTILS_H

#include <cstring>
#include <cstdarg>
#include <memory>

#define DEFER_1(x, y) x##y
#define DEFER_2(x, y) DEFER_1(x, y)
#define DEFER_3(x)    DEFER_2(x, __COUNTER__)
#define defer(code)   auto DEFER_3(_defer_) = defer_func([&](){ code; })

template<typename Function>
class Defer {

private:
    Function function;

public:
    explicit Defer(Function function) : function(function) {}

    ~Defer() { function(); }

};

template<typename Function>
Defer<Function> defer_func(Function function) {
    return Defer<Function>(function);
}

constexpr unsigned int hash(const char *str, int h = 0) {
    return !str[h] ? 5381 : (hash(str, h + 1) * 33) ^ str[h];
}

template<class Container>
void split(const std::string &str, Container &cont, char delim = ' ') {
    std::size_t current, previous = 0;
    current = str.find(delim);
    while (current != std::string::npos) {
        cont.push_back(str.substr(previous, current - previous));
        previous = current + 1;
        current = str.find(delim, previous);
    }
    cont.push_back(str.substr(previous, current - previous));
}

#endif //ARKOICOMPILER_UTILS_H
