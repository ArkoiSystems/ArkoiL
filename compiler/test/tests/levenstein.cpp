#include "../../include/utils/utils.h"
#include "gtest/gtest.h"

TEST(testLevenstein, nameDistance) {
    EXPECT_EQ(utils::levensteinDistance(std::string("test"), std::string("test")), 0);
    EXPECT_EQ(utils::levensteinDistance(std::string("ccfunction"),
                                        std::string("cc_function")), 1);
}