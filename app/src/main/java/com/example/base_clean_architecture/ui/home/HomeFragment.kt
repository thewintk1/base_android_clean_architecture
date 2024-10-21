package com.example.base_clean_architecture.ui.home

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.base_clean_architecture.common.base.BaseFragment
import com.example.base_clean_architecture.databinding.FragmentHomeBinding
import com.example.base_clean_architecture.service.UploadWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(FragmentHomeBinding::inflate) {
    override val viewModel: HomeViewModel by viewModels()
    var isLoading = false

    companion object {
        fun newInstance(
        ): HomeFragment {
            return HomeFragment()

        }
    }

    override fun initializeViews() {
        viewModel.loadUsers()

        binding?.apply {
            btnStart.setOnClickListener {
//                viewModel.fetchData()
                viewModel.refreshUsers()
//                setOnwTimeWorkRequest()
            }

            btnStop.setOnClickListener {
                viewModel.cancelJob()
            }

            viewModel.liveData.observe(viewLifecycleOwner) {
                tvResult.text = it.toString()
            }
            val adapter = UserAdapter()
            rvListUser.adapter = adapter
            rvListUser.layoutManager = LinearLayoutManager(context)

            // Quan sát StateFlow và cập nhật UI khi có danh sách mới
            lifecycleScope.launch {
                viewModel.users.collect { userList ->
                    Timber.tag("###BaseProject").d("Data User: ${userList.size}")
                    // Cập nhật danh sách người dùng vào RecyclerView
                    adapter.submitList(userList)
                    isLoading = false
                }
            }

            rvListUser.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    //Nếu item cuối cùng của layout = với giá trị cuối của recycleView thì ta gọi hàm LoadMore
                    if (!recyclerView.canScrollVertically(1) && !isLoading) {
                        // Đến cuối danh sách
                        isLoading = true
                        Timber.tag("###BaseProject").d("Last item $dx $dy")
                        adapter.addLoadingFooter()
                        viewModel.loadMoreUsers()
                    }
                }
            })

        }

    }

    private fun setOnwTimeWorkRequest() {

        val workManager = context?.let { WorkManager.getInstance(it) }
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        //Input and output
        val data: Data = Data.Builder()
            .putInt("KEY_COUNT_VALUE", 3000)
            .build()
        val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 5, TimeUnit.SECONDS)
            .setInputData(data)
            .setConstraints(constraints)
            .build()
        workManager?.enqueue(uploadRequest)
        workManager?.apply {
            getWorkInfoByIdLiveData(uploadRequest.id)
                .observe(viewLifecycleOwner) {
                    binding?.tvResult?.text = it.state.name
                    if (it.state.isFinished) {
                        val data = it.outputData
                        val message = data.getString(UploadWorker.KEY_WORKER)
                        Toast.makeText(context, message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
        }

    }

}
