//
// Created by timo on 7/29/20.
//

#pragma once

#include <algorithm>
#include <memory>
#include <vector>
#include <any>
#include <regex>

#include "../parser/allnodes.h"

#define DEFER_1(x, y) x##y
#define DEFER_2(x, y) DEFER_1(x, y)
#define DEFER_3(x)    DEFER_2(x, __COUNTER__)
#define defer(code)   auto DEFER_3(_defer_) = utils::defer_func([&](){code;})

namespace utils {

    template<typename Function>
    class Defer {

    private:
        Function function;

    public:
        explicit Defer(Function function) : function(function) {}

        ~Defer() { function(); }

    };

    template<typename Function>
    static Defer<Function> defer_func(Function function) {
        return Defer<Function>(function);
    }

    static std::vector<std::string> split(const std::string &input,
                                          const std::string &delimiter = "\\w+") {
        std::regex regex(delimiter);
        std::sregex_token_iterator first{input.begin(), input.end(), regex, -1}, last;
        return {first, last};
    }

    static std::string &ltrim(std::string &input, const std::string &chars = "\t\n\v\f\r") {
        input.erase(0, input.find_first_not_of(chars));
        return input;
    }

    static std::string &rtrim(std::string &input, const std::string &chars = "\t\n\v\f\r") {
        input.erase(input.find_last_not_of(chars) + 1);
        return input;
    }

    static std::string &trim(std::string &input, const std::string &chars = "\t\n\v\f\r") {
        return ltrim(rtrim(input, chars), chars);
    }

    static constexpr unsigned int hash(const char *input) {
        return *input ? (unsigned int) *input + 33 * hash(input + 1) : 5381;
    }

    template<typename Container, typename Type>
    static std::pair<bool, int> indexOf(const Container &container, const Type &element) {
        auto position = std::distance(container.begin(), std::find(container.begin(),
                                                                   container.end(), element));
        auto foundElement = position <= container.size();
        return {foundElement, position};
    }

    static std::string random_string(unsigned long length) {
        auto random_char = []() -> char {
            static const char charset[] =
                    "0123456789"
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    "abcdefghijklmnopqrstuvwxyz";
            static const unsigned long max_index = (sizeof(charset) - 1);
            return charset[rand() % max_index];
        };

        std::string string(length, 0);
        std::generate_n(string.begin(), length, random_char);
        return string;
    }

    // https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#C++
    template<typename T>
    static typename T::size_type levenshteinDistance(const T &source,
                                                     const T &target,
                                                     typename T::size_type insert_cost = 1,
                                                     typename T::size_type delete_cost = 1,
                                                     typename T::size_type replace_cost = 1) {
        if (source.size() > target.size())
            return levenshteinDistance(target, source, delete_cost, insert_cost, replace_cost);

        using TSizeType = typename T::size_type;
        const TSizeType min_size = source.size(), max_size = target.size();
        std::vector<TSizeType> lev_dist(min_size + 1);

        lev_dist[0] = 0;
        for (TSizeType i = 1; i <= min_size; ++i) {
            lev_dist[i] = lev_dist[i - 1] + delete_cost;
        }

        for (TSizeType j = 1; j <= max_size; ++j) {
            TSizeType previous_diagonal = lev_dist[0], previous_diagonal_save;
            lev_dist[0] += insert_cost;

            for (TSizeType i = 1; i <= min_size; ++i) {
                previous_diagonal_save = lev_dist[i];
                if (source[i - 1] == target[j - 1]) {
                    lev_dist[i] = previous_diagonal;
                } else {
                    lev_dist[i] = std::min(std::min(lev_dist[i - 1] + delete_cost,
                                                    lev_dist[i] + insert_cost),
                                           previous_diagonal + replace_cost);
                }
                previous_diagonal = previous_diagonal_save;
            }
        }

        return lev_dist[min_size];
    }

}