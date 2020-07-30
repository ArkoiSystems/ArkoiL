//
// Created by timo on 7/29/20.
//

#ifndef ARKOICOMPILER_UTILS_H
#define ARKOICOMPILER_UTILS_H

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

constexpr unsigned int str2int(const char *str, int h = 0) {
    return !str[h] ? 5381 : (str2int(str, h + 1) * 33) ^ str[h];
}

#endif //ARKOICOMPILER_UTILS_H
