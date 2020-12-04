#include "../../include/utils/utils.h"
#include "gtest/gtest.h"

TEST(LEVENSTEIN_TEST, positive_tests) {
    EXPECT_EQ(utils::levenshteinDistance(std::string("test"), std::string("test")), 0);
    EXPECT_NE(utils::levenshteinDistance(std::string("test_"), std::string("test")), 0);
    EXPECT_EQ(utils::levenshteinDistance(std::string("square_root"), std::string("square_rot")), 1);
}